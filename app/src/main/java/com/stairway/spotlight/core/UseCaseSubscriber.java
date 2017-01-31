package com.stairway.spotlight.core;


import rx.Subscriber;

/**
 * Created by vidhun on 16/07/16.
 */
public abstract class UseCaseSubscriber<T> extends Subscriber<T>
{
    protected BaseView view;

    public UseCaseSubscriber(BaseView baseView)
    {
        view = baseView;
    }

    @Override
    public void onCompleted()
    {

    }

    @Override
    public void onNext(T t)
    {
        onResult(t);
    }

    @Override
    public void onError(Throwable e)
    {
        onSessionNotFound();
        // TODO: Call based on error

        // if network error, onNetworkError()
        // if server error, on ServerError()
        // id sessionExpired, sessionExpired()
    }

    public void onSessionNotFound() {
    }

    public void onNetworkError()
    {
        Logger.v(this, "NETWORK ERROR");

        // TODO: Handle Network error.
    }

    public void onServerError() {
        Logger.v(this, "Server error");

        // TODO: Handle server error.
    }

    public void sessionExpired()
    {
        Logger.v(this, "Session expired.");

        // TODO: Handle session error
    }

    public abstract void onResult(T result);
}

