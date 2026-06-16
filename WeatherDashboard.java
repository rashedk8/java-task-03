import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.charset.StandardCharsets;

public class WeatherDashboard {
    
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    
    // Current weather data class
    static class WeatherData {
        String city;
        String country;
        double temperature;
        double feelsLike;
        int humidity;
        double windSpeed;
        String description;
        int pressure;
        int visibility;
        
        @Override
        public String toString() {
            return String.format(
                "╔════════════════════════════════════╗\n" +
                "║   %s, %s\n" +
                "╠════════════════════════════════════╣\n" +
                "║ Temperature: %.1f°C\n" +
                "║ Feels Like: %.1f°C\n" +
                "║ Condition: %s\n" +
                "║ Humidity: %d%%\n" +
                "║ Wind Speed: %.1f m/s\n" +
                "║ Pressure: %d hPa\n" +
                "║ Visibility: %d m\n" +
                "╚════════════════════════════════════╝",
                city, country, temperature, feelsLike, description, humidity, windSpeed, pressure, visibility
            );
        }
    }
    
    // Forecast data class
    static class ForecastData {
        String date;
        double maxTemp;
        double minTemp;
        String description;
        int humidity;
        double windSpeed;
        
        @Override
        public String toString() {
            return String.format(
                "  📅 %s | High: %.1f°C | Low: %.1f°C | %s | Humidity: %d%% | Wind: %.1f m/s",
                date, maxTemp, minTemp, description, humidity, windSpeed
            );
        }
    }
    
    /**
     * Fetch current weather for a city
     */
    public static WeatherData getCurrentWeather(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            String urlString = BASE_URL + "weather?q=" + encodedCity + "&appid=" + API_KEY + "&units=metric";
            
            String jsonResponse = makeAPICall(urlString);
            JSONObject json = new JSONObject(jsonResponse);
            
            WeatherData weather = new WeatherData();
            weather.city = json.getString("name");
            weather.country = json.getJSONObject("sys").getString("country");
            
            JSONObject main = json.getJSONObject("main");
            weather.temperature = main.getDouble("temp");
            weather.feelsLike = main.getDouble("feels_like");
            weather.humidity = main.getInt("humidity");
            weather.pressure = main.getInt("pressure");
            
            JSONObject wind = json.getJSONObject("wind");
            weather.windSpeed = wind.getDouble("speed");
            
            weather.visibility = json.getInt("visibility");
            
            JSONArray weather_arr = json.getJSONArray("weather");
            weather.description = weather_arr.getJSONObject(0).getString("main") + " - " + 
                                weather_arr.getJSONObject(0).getString("description");
            
            return weather;
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching current weather: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Fetch 5-day weather forecast for a city
     */
    public static ForecastData[] get5DayForecast(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            String urlString = BASE_URL + "forecast/daily?q=" + encodedCity + "&cnt=5&appid=" + API_KEY + "&units=metric";
            
            String jsonResponse = makeAPICall(urlString);
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray list = json.getJSONArray("list");
            
            ForecastData[] forecasts = new ForecastData[list.length()];
            
            for (int i = 0; i < list.length(); i++) {
                JSONObject day = list.getJSONObject(i);
                
                ForecastData forecast = new ForecastData();
                forecast.date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(
                    new java.util.Date(day.getLong("dt") * 1000)
                );
                
                JSONObject temp = day.getJSONObject("temp");
                forecast.maxTemp = temp.getDouble("max");
                forecast.minTemp = temp.getDouble("min");
                
                JSONArray weather_arr = day.getJSONArray("weather");
                forecast.description = weather_arr.getJSONObject(0).getString("main");
                
                forecast.humidity = day.getInt("humidity");
                forecast.windSpeed = day.getDouble("speed");
                
                forecasts[i] = forecast;
            }
            
            return forecasts;
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching forecast: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Make HTTP GET request to API
     */
    private static String makeAPICall(String urlString) throws Exception {
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)
        );
        
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        
        reader.close();
        return response.toString();
    }
    
    /**
     * Display weather dashboard
     */
    public static void displayDashboard(String city) {
        System.out.println("\n🌤️  WEATHER DASHBOARD 🌤️");
        System.out.println("═══════════════════════════════════════\n");
        
        // Fetch and display current weather
        System.out.println("📍 Fetching current weather for: " + city);
        WeatherData current = getCurrentWeather(city);
        
        if (current != null) {
            System.out.println(current);
        } else {
            System.out.println("❌ Could not fetch weather data. Please check city name.");
            return;
        }
        
        // Fetch and display 5-day forecast
        System.out.println("\n📅 5-DAY FORECAST:");
        System.out.println("═══════════════════════════════════════");
        ForecastData[] forecast = get5DayForecast(city);
        
        if (forecast != null) {
            for (ForecastData day : forecast) {
                System.out.println(day);
            }
        } else {
            System.out.println("❌ Could not fetch forecast data.");
        }
        
        System.out.println("\n═══════════════════════════════════════\n");
    }
    
    /**
     * Search and display weather for multiple cities
     */
    public static void searchMultipleCities(String[] cities) {
        System.out.println("\n🌍 WEATHER DASHBOARD - MULTIPLE CITIES 🌍");
        System.out.println("═════════════════════════════════════════════════════\n");
        
        for (String city : cities) {
            WeatherData weather = getCurrentWeather(city);
            
            if (weather != null) {
                System.out.printf(
                    "%-20s | 🌡️  %.1f°C | 💨 %.1f m/s | 💧 %d%% | %s\n",
                    city + ", " + weather.country,
                    weather.temperature,
                    weather.windSpeed,
                    weather.humidity,
                    weather.description
                );
            } else {
                System.out.printf("❌ %-40s | Could not fetch data\n", city);
            }
        }
        
        System.out.println("\n═════════════════════════════════════════════════════\n");
    }
    
    // Main method
    public static void main(String[] args) {
        // ⚠️ IMPORTANT: Replace with your OpenWeatherMap API key
        // Get free API key from: https://openweathermap.org/api
        
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       ☀️  WEATHER DASHBOARD APPLICATION ☀️        ║");
        System.out.println("║   Powered by OpenWeatherMap API                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        
        // Example 1: Single city weather
        System.out.println("\n[Example 1] Single City Weather:");
        displayDashboard("London");
        
        // Example 2: Multiple cities comparison
        System.out.println("\n[Example 2] Multiple Cities Comparison:");
        String[] cities = {"New York", "Tokyo", "Paris", "Sydney", "Dubai"};
        searchMultipleCities(cities);
        
        // Example 3: Interactive mode (uncomment to use)
        // interactiveMode();
    }
    
    /**
     * Interactive mode for user input
     */
    public static void interactiveMode() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        System.out.println("\n🔍 INTERACTIVE WEATHER SEARCH");
        System.out.println("═════════════════════════════════════════");
        
        while (true) {
            System.out.print("\nEnter city name (or 'exit' to quit): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("👋 Thank you for using Weather Dashboard!");
                break;
            }
            
            if (input.isEmpty()) {
                System.out.println("⚠️  Please enter a valid city name.");
                continue;
            }
            
            displayDashboard(input);
        }
        
        scanner.close();
    }
}
