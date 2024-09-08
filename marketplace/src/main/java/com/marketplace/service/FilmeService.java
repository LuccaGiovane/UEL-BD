package service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

import dto.FilmeDTO;

public class FilmeService {

    private static final String API_KEY = System.getenv("API_KEY_OMDB");

    public JSONObject getFilmeFromAPI(String nomeFilme) throws IOException
    {
        String url = "http://www.omdbapi.com/?t=" + nomeFilme.replace(" ", "%20") + "&apikey=" + API_KEY;

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Checa a resposta da API
        int responseCode = conn.getResponseCode();
        if (responseCode != 200)
        {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }


        Scanner scanner = new Scanner(conn.getInputStream());

        String jsonResponse = "";
        while (scanner.hasNext())
        {
            jsonResponse += scanner.nextLine();
        }
        scanner.close();

        return new JSONObject(jsonResponse);
    }

    public FilmeDTO mapJsonToDTO(JSONObject json)
    {
        FilmeDTO filme = new FilmeDTO();

        filme.setTitulo(json.getString("Title"));
        filme.setSinopse(json.getString("Plot"));
        filme.setPoster(json.getString("Poster"));
        filme.setAtores(json.getString("Actors"));
        filme.setDtLancamento(json.getString("Released"));
        filme.setAvaliacao(json.getDouble("imdbRating"));
        filme.setValor(json.getDouble("imdbRating") * 10); // Nota do filme * 10 pra dar o preço


        String generos = json.getString("Genre");
        filme.setGeneros(List.of(generos.split(", ")));


        String idiomas = json.getString("Language");
        filme.setIdiomas(List.of(idiomas.split(", ")));


        filme.setDuracao(Integer.parseInt(json.getString("Runtime").replace(" min", "")));

        return filme;
    }
}
