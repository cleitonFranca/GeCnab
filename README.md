# GeCnab
Projeto para disponibilização dos métodos de criação de remessa e leitura de retorno de arquivos cnab

Exemplo criação de remessa Banco do Brasil

```
File remessa = CBR641.createCBR641(titulos*);

* titulo é a entidade da biblioteca Bopepo base desse projeto, nessa entidade temos o: Cedente, sacado, endeços, conta bancária ou seja toda informação para criação da remessa. 

```

Exemplo leitura arquivo retorno Branco do Brasil

```
File fileExemplo = new File("resource/arquivoRetorno/CBR6436462012201712213.RET");
ProccessCBR643 retorno = CBR643.read(fileExemplo);
```
