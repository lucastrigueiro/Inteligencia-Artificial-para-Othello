# Inteligência artificial para Othello

Projeto com 2 players para o jogo Othello (Reversi).


## PlayerGreedy

Implementação do algoritmo Greedy para determinar a melhor jogada.


## PlayerMinMax

Implementação do algoritmo MinMax com Alpha-Beta Pruning. Também é possível determinar a profundidade do algoritmo MinMax.

## Estratégias

Tanto o PlayerGreedy quanto o PlayerMinMax estão implementados com a mesma estratégia, sendo 5 heurísticas e 3 jogadas especialistas.

**Especialistas**

- Tomar cantos diretamente
- Tática tomar cantos
- Prepara tomar cantos

**Heurísticas**

- Mobilidade
- Peças de vantagem
- Estabilidade
- WinLose
- Posicional (Possui 2 sub-heurísticas que alteram o valor do tabuleiro de acordo com o jogo)
	* Sub-Heurística: Acessos as laterais
	* Sub-Heurística: Adjacentes dos cantos

A descrição detahada das heurísticas, dos especialistas, do MinMax e da Alpha-Beta Pruning podem ser vistas no arquivo "Rascunho Relatório Othello.pdf": https://github.com/lucastrigueiro/Inteligencia-Artificial-para-Othello/blob/master/Rascunho%20Relat%C3%B3rio%20Othello.pdf

