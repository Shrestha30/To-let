If anyone want to setup or serve this project he/she have to do the steps mention below__
----

Though it is a android application project.
First he/she have to install Android Studio on this PC
and have also install SDK
and the API level should be 24-25.


Then he/she have to be familiar with Database system
and Firebase realtime database is used as this project database.

For the android emulator it's API level should be 24-25.

cloning this project in his/her local repo he/she can run this project in his pc
and can extend it. 

Firebase Related Information
----
To change linked firebase database one can follow this [video](https://www.youtube.com/watch?v=SRpdgIl8j-g) on youtube  

Database Structure(Direct Path names are mentioned without Quotes, Derived path names within quotation marks, Description within first brackets):  
  GeoFireLocations(used by GeoFire library to store hashcodes)  
   <pre>|--"HouseId"(Generated String: "User ID"+"("+"House name", It is the house key, used relational database model key generation concept)  
   |  |--(Generated child by GeoFire)  
   |--Users(contains all user related data)  
      |--"User ID"  
         |--Houses(Contains all houses/to-let data owned to the user)  
         |  |--"House Name"  
         |     |--bathrooms(int)  
         |     |--bedrooms(int)
         |     |--detail(String, data from optional detail)  
         |     |--floor(int)  
         |     |--isVerified(int)  
         |     |--lat(double, latitude of the house location)  
         |     |--lng(double, longitude of the house location)  
         |     |--name(String)  
         |     |--rent(int)  
         |     |--size(int)  
         |     |--streetAddress(String)  
         |--post  
         |  |--"post Id"  
         |     |--id(String)
         |     |--text(String)  
         |--profile  
            |--imageUrl(String)  
            |--name(String)  
            |--registerLatitude(double)  
            |--registerLongitude(double)  
            |--status(String)</pre>
   

Inside the codebase the code are well documented
----
for example

-> for the main page, the controller java file is MainActivity and xml file is activiy_main.

-> for the register page, the controller java file is RegisterActivity and xml file is activity_register.

-> for the starting page, the controller java file is StartActivity and xml file is activity_start.

-> for the login page, the controller java file is LoginActivity and xml file is activity_login.

-> for the account setting page, the controller java file is SettingActivity and xml file is activiy_setting.

-> for the Adding house in database button and google map API page, the controller java file is MapsAddHouseActivity and xml file is activity_maps_add_house.

-> for the Searching house from database button and google map API page, the controller java file is MapsSearchHouseActivity and xml file is activity_maps_serach_house.

-> for the retrieving or writing data to database , the controller java file is DatabaseInfoHelper



----
=> and in the main activity there were a a tab layout and view and fragments

the fragment conrtroller java file are Create_to-letFragment, TimlineFragment, ChatsFragment and there are also xml file according to this name.

and in the timline fragment there are recycler view the related java fill is TimlineFragment,TimlineDatamodel,TimelineAdapter



----
=> storing information or retreiving infromation in database the template used as a class

the class are Post,User

----
All the icons, background image are used here they are located in drawble file

----
You should install all SDK in android SDK tools in android studio


----
**Note related to Understanding some tricks implemented to make some functions optimized**  
HouseInfo.java and its class HouseInfo is intended for exchange of infoemation between activities. As all its members and methods are static one, can access it at anytime.  
DatabaseInfoHelper.java and its class DatabaseInfoHelper always keeps updated database snapshots. Its instance is created at the start of the application and is stored in the member mDatabaseInfoHelper of HouseInfo class. So, it can be easily accessed at anytime and be used to get updated snapshots of database.


FAQ
------
**#Qsn 1:**  if the dependency become outdate what should do?
 

**Ans:**  You need go to gardle build the change the dependency or update the dependency and sync it. Though its not necessary to update dependencies.

**#Qsn 2:** Project not running?
 

**Ans:** Check dependency,check sdk manager, check api level, check emulator .

**#Qsn 3:** Emulator Not working ?
 

**Ans:** Download or clone an emulator with API level matching with your project API level 

**#Qsn 4:** How to install or run project on apps in your phone ?
 

**Ans:** first you have to start your developer option and turn on USB debugging option then connect your phone with project with USB cable, and run your project selecting your phon as device. And there are lots of videos on it in youtube , you can search with this question title in youtube you will get lot of instruction resource

**#Qsn 5:** How to connect project with firebase database?

**Ans:** Go to tools in android studio, there is a option firebase, you can connect from there and next you have create a database 


