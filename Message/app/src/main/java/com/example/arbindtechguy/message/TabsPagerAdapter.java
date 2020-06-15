package com.example.arbindtechguy.message;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by arbindtechguy on 4/3/18.
 */

 class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm){
        super(fm);

    }
    public Fragment getItem(int position){
        switch (position){
            case 0: RequestsFragment requestsFragment = new RequestsFragment();
            return requestsFragment;

            case 1: ChatsFragment chatsFragment = new ChatsFragment();
            return chatsFragment;

            case 2: FriendsFragment friendsFragment = new FriendsFragment();
            return friendsFragment;

            default:
                return null;
        }

    }

    public int getCount(){
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0: return "Requests";

            case 1: return "Chats";

            case 2 : return "Friends";

            default:
                return null;

        }
    }
}
