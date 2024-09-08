package controller;

import java.io.IOException;
import org.json.JSONObject;
import service.FilmeService;

public class FilmeController
{

    private FilmeService filmeService = new FilmeService();

    public JSONObject buscarFilmePorNome(String nomeFilme) throws IOException
    {
        return filmeService.getFilmeFromAPI(nomeFilme);
    }
}
