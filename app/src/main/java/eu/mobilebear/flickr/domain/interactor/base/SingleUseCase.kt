package eu.mobilebear.flickr.domain.interactor.base

import com.stepstone.base.util.rx.EmptySingleObserver
import eu.mobilebear.babylon.rx.RxFactory
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation runs on the thread it was called in, and will return the result using a [DisposableSingleObserver]
 * that will execute its job in a background thread and will post the result in the UI thread.
 *
 * This use case is to be used when we expect a single value to be emitted via a [Single].
 */
abstract class SingleUseCase<Results, in Params>(
    @Companion.ThreadExecutor scheduler: Int, rxFactory: RxFactory
) : BaseReactiveUseCase(scheduler, rxFactory) {

    /**
     * Builds an [Single] which will be used when executing the current [SingleUseCase].
     */
    abstract fun buildUseCaseSingle(params: Params? = null): Single<Results>

    /**
     * Executes the current use case.
     *
     * @param observer [DisposableSingleObserver] which will be listening to the observer build
     * by [buildUseCaseSingle] method.
     * @param params Parameters (Optional) used to build/execute this use case.
     */
    fun execute(
        observer: DisposableSingleObserver<Results> = EmptySingleObserver(),
        params: Params? = null
    ) {
        val single = buildUseCaseSingleWithSchedulers(params)
        addDisposable(single.subscribeWith(observer))
    }

    /**
     * Builds a [Single] which will be used when executing the current [SingleUseCase].
     * With provided Schedulers
     */
    private fun buildUseCaseSingleWithSchedulers(params: Params?): Single<Results> {
        return buildUseCaseSingle(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
