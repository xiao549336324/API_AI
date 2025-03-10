# Gemini AI Word-Count Controller

A Spring Boot application that interfaces with Google's Gemini AI API to generate responses with exact word counts.

## Features

- Generate AI responses with precise word count control
- RESTful API endpoint for text generation
- Web interface for easy interaction
- Automatic word count adjustment to match requested length
- Environment-based configuration

## Prerequisites

- Java 17 or higher
- Maven
- Google Gemini API key

## Setup

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd [repository-name]
   ```

2. Configure environment variables:
   - Copy `.env.example` to `.env`
   - Add your Gemini API key to `.env`:
     ```
     GEMINI_API_KEY=your_api_key_here
     ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Usage

### Web Interface

Access the web interface at `http://localhost:8080`

### API Endpoint

```bash
POST /api/generate
Parameters:
- prompt: The text prompt for generation
- wordCount: Desired number of words (default: 100)
```

Example curl request:
```bash
curl -X POST "http://localhost:8080/api/generate?prompt=Tell%20me%20about%20AI&wordCount=50"
```

## Configuration

- Server port: 8080 (configurable in `application.properties`)
- Maximum word count: 1000 (configurable in frontend)
- API endpoint: `/api/generate`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── controller/
│   │           │   └── GeminiController.java
│   │           ├── service/
│   │           │   └── GeminiService.java
│   │           └── GeminiWebApplication.java
│   └── resources/
│       ├── static/
│       │   └── index.html
│       └── application.properties
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

[Your chosen license]

## Acknowledgments

- Google Gemini API
- Spring Boot Framework
