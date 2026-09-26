# Task Notifier

An Android application for managing institutional tasks, activities, and events between **faculty and students**.

The project includes separate student and faculty workflows, task assignment and tracking, event management, change requests, notifications, profiles, and data export.

> **Project Status**
>
> This is an older project that I built during my earlier Android development journey.
>
> The original Firebase project used by this application is no longer active, so the application will **not run with its original backend configuration**.
>
> The repository is preserved as part of my development history and to demonstrate my experience building a complete Firebase-backed Android application with multiple user roles and workflows.

---

## Overview

Task Notifier was designed to help students and faculty manage academic and institutional activities from a single Android application.

The application supports two primary user roles:

- Students
- Faculty

Faculty members can create and manage tasks and events, while students can track assigned activities, update their progress, and submit change requests.

---

## Features

### Authentication

- Student login
- Faculty login
- User registration
- Firebase Authentication integration

### Task Management

- Create tasks
- Assign tasks
- View assigned tasks
- View tasks created by the current user
- Edit tasks
- Delete tasks
- Track task progress
- Sort tasks by latest or oldest
- Filter task lists

Supported task states include:

- Not Complete
- In Progress
- Completed

### Task Change Requests

Users can request changes to assigned tasks and provide a reason for the requested modification.

Faculty or authorized users can:

- Review requests
- Approve requests
- Decline requests
- Track reasons for changes or delays

### Event Management

The application supports institutional event management including:

- Create events
- View all events
- View today's events
- View upcoming events
- View completed events
- Event date and time
- Venue information
- Event coordinators
- Registration links
- Additional event information

### Notifications

Firebase Cloud Messaging was used for application notifications related to tasks and other activity.

### Profiles

Users can manage profile information such as:

- Name
- Department
- Designation
- Account details

### Data Export

The project includes support for exporting task-related data using Apache POI.

---

## Tech Stack

The project was built using:

- **Android**
- **Kotlin**
- **Android SDK**
- **XML Layouts**
- **Data Binding**
- **Firebase Authentication**
- **Cloud Firestore**
- **Firebase Cloud Messaging**
- **Android Navigation Components**
- **Material Components**
- **Volley**
- **Gson**
- **Apache POI**

---

## Original Architecture

The application was structured around different functional areas including:

```text
Authentication
│
├── Student
├── Faculty
└── Registration

Application
│
├── Tasks
│   ├── Assigned Tasks
│   ├── Added Tasks
│   ├── Task Status
│   └── Change Requests
│
├── Events
│   ├── All Events
│   ├── Upcoming Events
│   └── Completed Events
│
├── Notifications
├── Profile
└── Settings
```

Firebase Firestore was used as the application's cloud database.

---

## Project Structure

```text
cp-activity-notifier/
│
├── app/
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com.example.bfgiactivitynotifier/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   │
│   ├── libs/
│   └── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## Build Configuration

The project was originally configured with:

```text
Compile SDK: 33
Target SDK: 33
Minimum SDK: 28
```

Application package:

```text
com.example.bfgiactivitynotifier
```

Major build dependencies included:

```text
Android Gradle Plugin 7.4.0
Kotlin 1.7.20
Google Services Plugin 4.3.14
Secrets Gradle Plugin 2.0.1
```

---

## Firebase Configuration

The application originally depended on:

- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Messaging

### Important

The Firebase project originally connected to this application **no longer exists**.

As a result:

- Authentication will not work with the original configuration.
- Firestore data is no longer available.
- Push notifications will not work.
- The application cannot be run end-to-end using the original backend.

The source code remains available for reference and portfolio purposes.

---

## Running the Project

If you want to experiment with the application locally, you would need to create a new Firebase project.

### 1. Clone the repository

```bash
git clone https://github.com/himanshu240601/cp-activity-notifier.git

cd cp-activity-notifier
```

### 2. Open the project

Open the project in Android Studio and allow Gradle to sync.

### 3. Create a Firebase project

Create a new project in Firebase and register an Android application using:

```text
com.example.bfgiactivitynotifier
```

Enable:

```text
Firebase Authentication
Cloud Firestore
Firebase Cloud Messaging
```

Download the new:

```text
google-services.json
```

and place it inside:

```text
app/google-services.json
```

### 4. Recreate the database configuration

Because the original Firestore database is no longer available, the required collections and data structure would need to be recreated based on the application models and Firestore calls in the source code.

### 5. Build the project

```bash
./gradlew assembleDebug
```

Or run the application directly through Android Studio.

---

## What I Learned

This was one of my earlier full Android projects and gave me hands-on experience building an application beyond basic CRUD functionality.

Through this project I worked with:

- Role-based application workflows
- Firebase Authentication
- Cloud Firestore
- Push notifications with FCM
- Task lifecycle management
- Approval and change-request workflows
- Android navigation
- Network requests
- Local and cloud data handling
- Data export functionality
- Building an Android application from requirements through implementation

The project also helped build the foundation for the mobile engineering work I continued doing across Android, iOS, and cross-platform technologies.

---

## Project History

This repository is intentionally kept close to its original implementation.

Rather than rewriting the application using modern Android architecture, the repository is preserved to show the code and development practices I was using at that stage of my development journey.

My newer projects represent my current engineering practices and architecture choices.

---

## Author

**Himanshu Goyal**

GitHub: [@himanshu240601](https://github.com/himanshu240601)

---

## License

No open-source license is currently specified for this repository.
