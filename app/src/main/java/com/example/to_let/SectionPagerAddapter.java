package com.example.to_let;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
//
public class SectionPagerAddapter extends FragmentPagerAdapter {

    public SectionPagerAddapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    //the homepage 3 tab here
    public Fragment getItem(int position) {

                 switch (position)
                 {
                      case 0:
                      // AllUserFragment allUserFragment=new AllUserFragment();
                       Create_TOLET create_toletfragment=new Create_TOLET();
                        return  create_toletfragment;
                     case 1:
                         TimlineFragment timeLinefragment=new TimlineFragment();
                         return timeLinefragment;
                     case 2:
                         ChatsFragment chatsfragment=new ChatsFragment();
                         return chatsfragment;

                     default:
                         return null;
                 }


    }

    @Override
    //number of tabs
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int Position)
    {
        switch (Position){
            case 0:
              return "Create-TOLET";
            case 1:
                return "Timeline";
            case 2:
                return "Chats";

            default:
                return null;

        }
    }

}
