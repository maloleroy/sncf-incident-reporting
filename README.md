# SNCF Incidents Mobile App Project

This repository contains the source code for SNCF's mobile application designed for on-board personnel to report and manage incidents. The application is built using Kotlin, providing a modern, android-first solution for incident management.

## Project Overview

The SNCF Incidents Mobile App is a comprehensive solution that enables:
- Real-time incident reporting
- Trip tracking
- Speech-to-text functionality leveraging AI

## Repository Structure

Here are some of the most important files and directories of the project
```
.
├── appKotlin/AppV1   # Kotlin application
│   └── app/src/main/java/com/example/appv1/
│       ├── api/      # API calls logic
│       ├── data/     # Incident synchronization
│       ├── domain/   # Data models and structures
│       └── ui/       # User interface
│           ├── components/
│           ├── screens/
│           ├── theme/
│           └── util/
├── back              # FastAPI backend
│   ├── main.py       # Definition of the routes
│   └── sql/          # SQL scripts
├── .gitlab-ci.yml    # CI/CD configuration
└── README.md         # This file
```

## Getting Started

- For detailed setup instructions, development guidelines, and technical documentation on the Kotlin app, please refer to the app's [README.md](appKotlin/AppV1//README.md) file.
- For instructions on the 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.