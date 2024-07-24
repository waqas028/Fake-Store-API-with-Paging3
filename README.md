# Fake Store API with Paging 3 in Compose (MVVM & Hilt), Generic Paging Source which Supports Multiple API
This is an Android application that demonstrates the use of Jetpack Compose with Paging3 library to fetch and display products from the Fake Store API. The project follows the MVVM (Model-View-ViewModel) design pattern and Dependency Injection (Hilt).

## Platiz Fake Store API
- https://fakeapi.platzi.com

# Demo Video
![Screen_recording_20240709_184655](https://github.com/waqas028/Fake-Store-API-with-Paging3/assets/96041723/b4b78b02-8e66-4c96-ad2f-9ce5af2b1de0)

## Features
- Fetches products from the Fake Store API
- Uses `Paging3` library to handle pagination
- Generic Paging Source with Generic Repo
- Displays products in a Jetpack Compose UI
- Implements `MVVM` design pattern for better code organization
- `LinearOutSlowInEasing` and `FastOutSlowInEasing` Animation

### GenericPagingDataSource
```kotlin
class GenericPagingDataSource<T : Any, R>(
    private val apiService: suspend (R) -> List<T>,
    private val requestData: R,
    private val updateRequest: (R, Int) -> R
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val nextPage = params.key ?: 1
            val updatedRequestData = updateRequest(requestData, nextPage)
            val responseData = apiService(updatedRequestData)
            LoadResult.Page(
                data = responseData,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (responseData.isNotEmpty()) nextPage + 1 else null
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}
```
### GenericPagingRepository
```kotlin
class GenericPagingRepository @Inject constructor() {
    fun <T : Any, R> getPagingData(
        requestData: R,
        updateRequest: (R, Int) -> R,
        fetchData: suspend (R) -> List<T>
    ): Flow<PagingData<T>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 2,
            initialLoadSize = 10
        ),
        pagingSourceFactory = {
            GenericPagingDataSource(
                apiService = fetchData,
                requestData = requestData,
                updateRequest = updateRequest
            )
        }
    ).flow
}
```

### HomeViewModel
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val genericPagingRepository: GenericPagingRepository,
    private val apiInterface: APIInterface
) : ViewModel() {
    var getProducts by mutableStateOf<Flow<PagingData<Product>>>(flowOf(PagingData.empty()))

    fun getProducts() = viewModelScope.launch {
        getProducts = genericPagingRepository.getPagingData(
            requestData = RequestData(offset = 1, limit = 10),
            updateRequest = { request, page -> request.copy(offset = page) },
            fetchData = { request -> apiInterface.getProducts(request.offset, request.limit) }
        )
    }
}
```

## Contributing

Contributions are welcome! Please follow these steps:

- Fork the repository.
- Create a new branch (git checkout -b feature-branch).
- Commit your changes (git commit -m 'Add some feature').
- Push to the branch (git push origin feature-branch).
- Open a pull request.

## Contact

For any inquiries, please contact waqaswaseem679@gmail.com.

