# Club Project Portal

A platform that connects **students** and **clubs** for collaborative projects, events, and skill development.  
Students can showcase their skills, join projects, and explore clubs, while clubs can manage members, publish projects, and organize events.


## Features

- **Students**
  - Register and log in
  - Maintain profile with skills & interests
  - Browse and join projects

- **Clubs**
  - Register and log in
  - Create and manage projects
  - Host events
  - Collaborate with other clubs

- **Projects**
  - Define required skills and member limits
  - Accept join requests
  - Track project members

- **Events**
  - Clubs can post and manage events
  - Students can browse upcoming events

- **Collaborations**
  - Clubs can collaborate with other clubs
  - Many-to-many schema support


## Tech Stack

- **Backend:** Java (JDBC/Servlets)  
- **Database:** MySQL (UTF-8)  


## Database Schema

- `Student` – student accounts and profiles  
- `Club` – club accounts and metadata  
- `Project` – project listings with required skills and limits  
- `ProjectMembers` – assignments of students to projects  
- `JoinProject` – join requests for projects  
- `Event` – events created by clubs  
- `ClubCollaborations` – links between clubs  

Schema file: [`ProjectPortal.sql`](./ProjectPortal.sql)


## Quick Setup

1. Clone this repository  
2. Import the SQL schema into MySQL  
3. Update DB credentials in `ClubProjectPortal.java`  
4. Compile and run the Java file  


## Future Enhancements

- Stronger authentication (passwords)  
- Web frontend  
- Notifications and messaging 