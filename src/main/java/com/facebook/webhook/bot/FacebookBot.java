package com.facebook.webhook.bot;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;

public class FacebookBot {
    public static void main(String[] args) {
        String accessToken = "EAAHzy6wTM2sBO11KEnP7fRcxZCYVeM9cAG78KHZAZAwwn6ZA37Ufeht24ClUqnAxcq7KpAJgNZANdJhHSs1tRC3nFWzpum3RxHhuUIIc7r8EpYEQvxP9Hmm0BHUrPon1BRKDCyjCgt3illNP0vclaFg5gV8Va3db5xHBKCn8RUAbjcUGLUSJTubZAMpzZCaGCRVHAZDZD";
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

        // Fetch the current user's profile
        User user = facebookClient.fetchObject("me", User.class);
        System.out.println("User name: " + user.getName());
    }
}
