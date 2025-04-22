# SNCF Incidents Mobile App Project

This repository contains the source code for SNCF's mobile application designed for on-board personnel to report and manage incidents. The application is built using React Native and Expo, providing a modern, cross-platform solution for incident management.

## Project Overview

The SNCF Incidents Mobile App is a comprehensive solution that enables:
- Real-time incident reporting
- Trip tracking
- Speech-to-text functionality for hands-free operation
- Offline-first operation with automatic synchronization
- User account management
- Feedback collection

## Repository Structure

```
.
├── app/                    # Main application code and documentation
│   ├── src/              # Source code
│   ├── components/       # Reusable React components
│   ├── services/         # Business logic and services
│   ├── types/            # TypeScript type definitions
│   ├── assets/           # Static assets (images, fonts, etc.)
│   └── README.md         # Detailed documentation
├── .gitlab-ci.yml        # CI/CD configuration
└── README.md             # This file
```

## Getting Started

For detailed setup instructions, development guidelines, and technical documentation, please refer to the [app/README.md](app/README.md) file.

## Development

The project uses:
- React Native with Expo for cross-platform development
- TypeScript for type safety
- Vosk for speech-to-text functionality
- Docker for containerized development

## Contributing

We welcome contributions to this project. Please refer to the [Contributing Guidelines](app/README.md#contributing) in the app directory for detailed information on how to contribute.

## License

This project is licensed under the MIT License - see the [LICENSE](app/LICENSE) file for details. 