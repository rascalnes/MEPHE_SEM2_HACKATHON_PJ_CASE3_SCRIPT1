# 1. Клонировать репозиторий
git clone https://github.com/rascalnes/MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES.git

cd MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES

# 2. Создать .env файл из примера
cp .env.example .env <!-- doesn't work yet -->

# 3. Собрать приложение
./gradlew clean build

# 4. Запустить PostgreSQL и Adminer
docker-compose up -d

# 5. Запустить приложение
./gradlew run <!-- doesn't work yet -->

# Или запустить через Docker Compose (полный стек)
docker-compose up --build