# Prerequisite
APP_NAME="lms"
APP_VERSION="0.0.1"
APP_DIR="/Users/boberoi/Documents/web/lms/lms_back"
DB_URL="jdbc:mysql://localhost:3306/db"
DB_USERNAME="root"
DB_PASSWORD="khushi123"

# Function to check for prerequisites
check_prerequisites() {
    echo "Checking for prerequisites..."
    
    # Check for Java
    if ! command -v java &> /dev/null; then
        echo "Java is not installed. Please install Java JDK (version 20 or higher)."
        exit 1
    fi

    # Check for Maven
    if ! command -v mvn &> /dev/null; then
        echo "Maven is not installed. Please install Maven."
        exit 1
    fi

    echo "All prerequisites are met."
}

# Function to configure properties
configure_properties() {
    echo "Configuring application properties..."
    
    # Create a configuration file or modify existing properties
    cat <<EOL > /Users/boberoi/Documents/web/lms/lms_back/src/main/resources/application.properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
server.port=8081
EOL

    echo "Properties configured."
}

# Function to build and run the application
build_and_run() {
    echo "Building the application..."
    cd "$APP_DIR" || exit
    mvn clean package

    echo "Running the application..."
    JAR_FILE="target/LMS-0.0.1-SNAPSHOT.jar"
    java -jar "$JAR_FILE"
}

# Main script execution
check_prerequisites
configure_properties
build_and_run

