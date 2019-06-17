package com.example.algamoney.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	public Categoria criarCategoria(Categoria categoria) {

		Categoria cat = categoriaRepository.findByNome(categoria.getNome());
		if (cat != null) {
			throw new DataIntegrityViolationException("Já existe uma categoria com o nome informado");
		}

		categoria.setCodigo(null);

		return categoriaRepository.save(categoria);
	}

	public Categoria alterarCategoria(Categoria categoria) {

		if (categoria == null || categoria.getCodigo() == null) {
			throw new DataIntegrityViolationException("Deve ser informado a categoria para alteração");
		}

		Categoria cat = categoriaRepository.findByNome(categoria.getNome());
		if (cat != null && !cat.getCodigo().equals(categoria.getCodigo())) {
			throw new DataIntegrityViolationException("Já existe uma outra categoria com o nome informado");
		}
		if (cat.equals(categoria)) {
			return cat;
		}

		return categoriaRepository.save(categoria);
	}
}
