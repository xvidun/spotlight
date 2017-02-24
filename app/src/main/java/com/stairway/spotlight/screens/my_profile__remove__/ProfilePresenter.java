//package com.stairway.spotlight.screens.my_profile;
//
//import com.stairway.data.source.user.UserSessionResult;
//import com.stairway.data.source.user.gson_models.UserResponse;
//
//import java.io.File;
//
//import rx.Subscription;
//import rx.schedulers.Schedulers;
//import rx.subscriptions.CompositeSubscription;
//
///**
// * Created by vidhun on 03/01/17.
// */
//
//public class ProfilePresenter implements ProfileContract.Presenter {
//    private ProfileContract.View profileView;
//    private CompositeSubscription compositeSubscription;
//    private UpdateProfileDPUseCase updateProfileDPUseCase;
//
//    public ProfilePresenter(UpdateProfileDPUseCase updateProfileDPUseCase) {
//        this.updateProfileDPUseCase = updateProfileDPUseCase;
//        this.compositeSubscription = new CompositeSubscription();
//    }
//
//    @Override
//    public void uploadProfileDP(File image, UserSessionResult userSession) {
//        Subscription subscription = updateProfileDPUseCase.execute(image, userSession)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(profileView.getUiScheduler())
//                .subscribe(new seCaseSubscriber<UserResponse>(profileView) {
//                    @Override
//                    public void onResult(UserResponse result) {
//                        profileView.updateProfileDP(result.getUser().getProfileDP().replace("https://", "http://"));
//                    }
//                });
//        compositeSubscription.add(subscription);
//    }
//
//    @Override
//    public void init(UserSessionResult userSession) {
//        if(userSession.getProfileDP()!=null && !userSession.getProfileDP().isEmpty())
//            profileView.setProfileDP(userSession.getProfileDP());
//        else
//            profileView.setProfileDP("");
//    }
//
//    @Override
//    public void attachView(ProfileContract.View view) {
//        this.profileView = view;
//    }
//
//    @Override
//    public void detachView() {
//        this.profileView = null;
//    }
//}
