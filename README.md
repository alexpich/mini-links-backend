# MiniLinks Backend

**Project Description**: This is the backend component of the high-performance URL shortening service implemented using Spring Boot, React, Next.js, and NoSQL. The project includes a sophisticated ID generator that uses a custom algorithm to convert IDs into a compact base62 format, enabling the creation of concise and unique shortened URLs. Additionally, an integrated Java Spring-based API rate limiter efficiently manages incoming requests, enhancing system stability by tracking token availability and timed refills. The backend also utilizes Redis as an in-memory caching layer to optimize the retrieval of frequently accessed URLs, reducing database load and improving overall response times.

## Installation Instructions

1. Clone the repository.
2. Run the main class `MinilinksApplication.java` to start the backend.

## Usage


1. Make sure Redis is running. If not, follow the [Redis installation instructions](https://redis.io/docs/getting-started/installation/).
2. Visit [http://localhost:3000](http://localhost:3000) in the browser.*
3. Enter the desired URL, making sure to include the prefix `http://` or `https://`.
4. Click the button to generate a new shortened link.

*For the front setup, please follow these steps:

1. Visit the [MiniLinks Frontend Repository](https://github.com/alexpich/mini-links-frontend) for instructions on setting up the frontend.


## Features

- URL shortening for convenient link sharing.
- Fast response times for optimal user experience.
- Built-in rate limiting to prevent abuse and ensure system stability.

## Technologies Used

- Spring Boot
- React
- Next.js
- Redis
- Tailwind CSS

## Contributing

Currently, no specific guidelines for contributing have been established. However, contributions are welcome.

## License

No specific license has been chosen for this project.

## Author

This project is authored by Alex Pich.

---
