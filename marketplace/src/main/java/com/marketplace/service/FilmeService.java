package com.marketplace.service;

import com.marketplace.dao.FilmeDAO;
import com.marketplace.dto.FilmeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class FilmeService
{

    @Autowired
    private FilmeDAO filmeDAO;

    public void inserirFilme(FilmeDTO filme) throws SQLException
    {
        filmeDAO.inserirFilme(filme);
    }

    public List<FilmeDTO> listarFilmes()
    {
        return filmeDAO.listarFilmes();
    }
}
