# Stream 

We are an Android application aimed to help students work efficiently and effectively in group projects.

# Team Members 
1. Kevin Wong 
2. Robyn Castro
3. Catherine Lee 
4. Omar Mohammed
5. Rohini Goyal
6. Jane MacGillivray
7. Xingyu Tao

# How to find Documents 
- Documents can be found at https://github.com/omarbakker/Stream-app/tree/master/docs

# Issues with the application
- Bugs are listed under Issues tab on GitHub

# Developer Section

# How to find source code 
- All source code is located in the directory app/src/main: https://github.com/omarbakker/Stream-app/tree/master/app/src/main/

# How to check out source code and run project
1. Install Android Studio on computer
2. Set up Emulator or have an Android Device
3. Clone the repository from GitHub by: git clone https://github.com/omarbakker/Stream-app on Terminal/ Git Bash
4. Open the project on Android Studio and wait for Gradle to sync
5. Run the Android app on emulator or plug in Android Device and run app on device

# How to run tests
1. Clone the repo from GitHub by: git clone https://github.com/omarbakker/Stream-app on Terminal/ Git Bash
2. On Terminal or Git Bash type in the command git fetch --all to get all branches of the repository
3. On Terminal or Git Bash type in the command git checkout UnitTests to get to the Unit testing branch (this will be move to master before final demo)
4. Open the project on Android Studio and wait for Gradle to sync
5. Go to directory app/src/androidTest/java/com/test/stream/stream to see all Unit Tests
6. Open the Unit Test file e.g. ProjectsTest.java
7. Right click on the class name and click Run. For instance if you are trying to run ProjectsTest.java right click ProjectsTest and click "Run ProjectsTest"

# Structure of source code directory
- app/src/main/assets folder includes all fonts used for the project
- AndroidManifest.xml contains settings for the project such as listing all Activities and Fragments as well as themes
- app/src/main/res folder contains all images used in the application. Images will be stored on drawable or mipmap folder 
- app/src/main/res/layout folder contains all the xml files for the application. These xml files are inflated to display the UI of the application such as PinBoard, Tasks, Projects, etc
- app/src/main/java/com/test/stream/stream/Controllers contains all the Java files to interact with Firebase
- app/src/main/java/com/test/stream/stream/Objects contains all the object declarations of the project. For instance, PinMessage will have string properties to store title, color and description. These objects will make it easier to store data into the database.
- app/src/main/java/com/test/stream/stream/UI contains all the activities of our application. These activities display all the UI such as buttons, lists, popup dialogs and many more so that the users can interact with the application 
- app/src/main/java/com/test/stream/stream/UIFragments contains all the fragments for navigation when clicking on the specified button on the toolbar
- app/src/main/java/com/test/stream/stream/Utilities contains all the enums, callbacks and general database helper functions for the application

# Design Patterns used
- We are using the Singleton design pattern for the database Managers (BoardManager, CalendaManager, DataManager, ProjectManager, TaskManager, UserManager) because there are a lot of dependencies with the data in the database. Since we want to be able to access objects while browsing through the application, we applied the Singleton design pattern to these classes.
