package com.stairway.spotlight.screens.message;

import com.stairway.spotlight.core.Logger;
import com.stairway.spotlight.db.ContactStore;
import com.stairway.spotlight.models.ContactResult;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by vidhun on 08/01/17.
 */
public class GetNameUseCase {
    private ContactStore contactStore;

    public GetNameUseCase(ContactStore contactStore) {
        this.contactStore = contactStore;
    }

    public Observable<String> execute(String username){
        Logger.d(this, "getting name for:"+username);
        return Observable.create(subscriber -> {
        });
    }
}