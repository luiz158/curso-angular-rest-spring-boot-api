CREATE TABLE usuario_recuperar_senha (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	token VARCHAR(50) NOT NULL,
	codigo_usuario BIGINT(20) NOT NULL,
	data_solicitacao DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;