
# The magic of backend independent UI testing in Android

> Medium article: https://link.medium.com/p5159tsfkU

Have you ever asked yourself how backend independent UI testing in Android works? Yes? Then please continue reading… ;)

As an app developer, from time to time you get the chance to integrate external APIs. When it comes to our customer projects, it is often the customer's backend from which we are able to retrieve corresponding data.

If we need to implement a new feature, the best-case scenario would be that the API has already been adapted on a staging system, so we're able to connect to it. The reality, however, is that new features often have to be implemented by the customer, as well. So unfortunately, there is seldom an API version available that reflects the new behaviour.

There are two different approaches to start the development:

* You have a MockServer that simulates the behaviour of the real server and can simply be extended with the new function (assuming there is an agreement on how the API will be implemented).
* You mock the data within the app and return it directly during test runtime.

**Both options have their advantages & disadvantages:**

![](https://cdn-images-1.medium.com/max/800/1*3VJI2GwrUstUe9lu3XnheQ.png)

I am guessing that most of you are familiar with "mocking" or how MockServers work. If not, the web offers a wide variety of articles on this matter. For example, check out the following Wikipedia article on Mock objects.

Since the approach of mocked data within the app is often only described for JUnit-Tests, I would like to explain this option with regard to UI tests in this article.


## Dependency Injection & MVVM

The key to all this is Dependency Injection, i.e. the insertion of defined implementations at runtime and the MVVM app architecture. We currently use Dagger to insert dependencies, but the same should apply to _[koin](https://insert-koin.io/)_ and other libraries.

The [MVVM](https://en.wikipedia.org/wiki/Model–view–viewmodel) describes the differentiation between model, view and view model, which makes it possible to test them.

## How does it all work now?

We use a custom class that inherits from the application class and thus represents the root context of the app. In this class, Dagger is initialized, so we can inject every dependency from there to the desired places.


### JUnit Tests (for comparison)
Within the JUnit tests, the desired injection can simply be overwritten (at setUp()) to provoke a desired behaviour. This way almost all test cases can be mapped.

```kotlin
class MainViewModelTest {

    private lateinit var apiService: ApiService

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        // mock service
        apiService = mock()

        // define dagger dependencies
        val appComponent = DaggerAppComponent.builder()
            .apiModule(object : ApiModule() {
                override fun provideApiService(): ApiService {
                    return apiService
                }
            })
            .build()

        // set appComponent
        Injector.INSTANCE.set(appComponent)
    }

    @After
    fun tearDown() {

        // clear dependencies
        Injector.INSTANCE.clear()
    }

    @Test
    fun `get response data | apiService is requested`() {

        // init viewModel
        val viewModel = MainViewModel()

        // trigger action
        viewModel.loadData()

        // check assertions
        verify(apiService, times(1)).getData(any<(JSONArray) -> Unit>(), any<(ANError) -> Unit>())
    }

    @Test
    fun `get response data | success | response is returned`() {

        // define mock behavior
        doAnswer {

            val myArray = JSONArray()
            myArray.put("test")
            val successLambda = it.arguments[0] as (JSONArray) -> Unit
            successLambda(myArray)
            null
        }.whenever(apiService).getData(any<(JSONArray) -> Unit>(), any())

        // init viewModel
        val viewModel = MainViewModel()

        // trigger action
        viewModel.loadData()

        // check assertions
        assertEquals("test", viewModel.data.value)
    }

    @Test
    fun `get response data | error | '-no data -' is returned`() {

        // define mock behavior
        doAnswer {
            val errorLambda = it.arguments[1] as (ANError) -> Unit
            errorLambda.invoke(ANError("test message"))
        }.whenever(apiService).getData(any(), any<(ANError) -> Unit>())

        // init viewModel
        val viewModel = MainViewModel()

        // trigge action
        viewModel.loadData()

        // check assertions
        assertEquals("- no data -", viewModel.data.value)
    }
}
```

### UI Tests
Unfortunately, the procedure used for the JUnit tests does not work within the UI tests. Because within these tests no mock data (using [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)) can be created, another variant for the test procedure must be used for the Dagger Dependencies.

However, since we do not have direct access to the application (only after the launch of the activity, at which time the Dagger initialization has already been performed), we must consider another construct.


#### 1. Custom TestApplication Class for UI Tests

First, we have to define our own application class for our test runtime. This is necessary because we can't initialize a mock as injected dependency, as in the setUp() method shown above.

#### 2. Test runner
In order that the UI test also uses the newly created application (and not the original one), we must write our own small TestRunner. This doesn't really contain much, but only refers to our TestApplication:

```kotlin
open class MockTestRunner : AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle) {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        super.onCreate(arguments)
    }


    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestAppApplication::class.java.name, context)
    }
}
```

Within the build.gradle (app), we only have to use this runner for our tests:

```gradle
android {
    ...

    defaultConfig {
        ...
        testInstrumentationRunner "com.thepeaklab.backendindependentuitests.core.MockTestRunner"
        ...
    }

    ...
}
```

#### 3. Mocking Data

As described above, we can't inject a mock, so we must do our own implementation of the interface in order to adapt it to the test runtime.
The mocked implementation could look like this:

```kotlin
object MockApiService {

    // all available methods
    enum class Method {
        getData
    }

    // expatiations
    private val expectationMap = mutableMapOf<Method, Expectation>()


    /**
     * set expectations
     *
     */
    fun setExpectation(method: Method, expectedResult: Expectation) {
        expectationMap.put(method, expectedResult)
    }


    /**
     * reset all expectation so the default will be used
     *
     */
    fun resetExpectation() {
        expectationMap.clear()
    }


    /**
     * hold repository object
     * and return mocked data
     *
     */
    val service = object : ApiService {

        override fun getData(onResponse: (response: JSONArray) -> Unit, onError: (error: ANError) -> Unit) {
            expectationMap.get(Method.getData)?.let {
                it.success?.let {
                    val mockSuccess = it.invoke()
                    onResponse(mockSuccess.result as JSONArray)
                    return
                }
                it.failure?.let {
                    val mockFailure = it.invoke()
                    onError(ANError(mockFailure.error))
                    return
                }
            }

            val myArray = JSONArray()
            myArray.put("test")

            return onResponse(myArray)
        }
    }
}
```

The mocked file consists of the expectations and the actual (mock) implementation under _service_.

By setting an expectation, the return value can be influenced, so we can now also adjust it within the UI test for the test runtime (similar to the unit tests before).


#### 4. TestImplementation of the Dagger initialization in the TestApplication.kt.
Now that we have a test implementation, we have to equip Dagger with it as well. We do this again in the TestApplication:

```kotlin
class TestAppApplication : Application() {

    // initialisieren des Mock-ApiService
    var mockApiService = MockApiService

    /**
     * onCreate
     *
     */
    override fun onCreate() {
        super.onCreate()

        // init dependency injection
        val appComponent = DaggerAppComponent.builder()
            .apiModule(object : ApiModule() {
                override fun provideApiService(): ApiService {
                    return mockApiService.service
                }
            })
            .build()

        // set AppComponent
        Injector.INSTANCE.set(appComponent)
    }
}
```

### Ready for UI-Testing

After all these steps, we are finally able to write UI tests that check how the UI behaves even if there is no implementation from the backend yet.

The tests could look like this:

```kotlin
class MainViewTest {

    @Rule
    @JvmField
    var testRule = IntentsTestRule(MainActivity::class.java, true, false)

    // nedded for correct handling of 'postValue' in LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {

        // reset expectations so it's clean on new test
        MockApiService.resetExpectation()
    }


    @Test
    fun request_data___success___response_is_shown() {

        // define test data
        val myArray = JSONArray()
        myArray.put("the test response data")

        // set expectation
        MockApiService.setExpectation(MockApiService.Method.getData, Expectation(success = { MockSuccess(myArray) }))

        // launch activity
        testRule.launchActivity(null)

        // check assertions
        "the test response data".checkIsDisplayedAsText()
    }


    @Test
    fun request_data___failure___no_data_text_is_shown() {

        // set expectation
        MockApiService.setExpectation(MockApiService.Method.getData, Expectation(failure = { MockFailure("some error") }))

        // launch activity
        testRule.launchActivity(null)

        // check assertions
        "- no data -".checkIsDisplayedAsText()
    }
}
```

# And that's it! :)

Now we can write UI tests that can simulate the desired response by means of the expectation. Thus, how the UI of the app behaves in different scenarios can now be tested.

It should be said again, however, that this is now a pure UI test and not an integration test, as it is often presented.

Since no real request is sent, the network interface cannot be checked this way, only the possible use cases.
