import random as rand
import math
import numpy as np

provinces = ["JUNGLE","TUNDRA","SAVANNAH"]
party = ["OWL","TIGER","LYNX","BUFFALO","LEOPARD","TURTLE","JACKALOPE"]
tables = [["1","2","3"],["4","5","6"],["7","8","9"]]



f = open('votes.csv', 'w')


for i in range(500000):
  p = math.floor(rand.uniform(0,3))
  t =  math.floor(rand.uniform(0,3))
  c1 =  math.floor(rand.uniform(0,7))
  c2 =  math.floor(rand.uniform(0,7))
  while (c2 == c1):
    c2 =  math.floor(rand.uniform(0,7))

  c3 =  math.floor(rand.uniform(0,7))
  while (c3 == c1 or c3 == c2):
    c3 =  math.floor(rand.uniform(0,7))

  f.write(tables[p][t] + ";" + provinces[p] + ";" + party[c1] + "|" + str(math.floor(rand.uniform(1,6) )) + "," + party[c2] + "|" + str(math.floor(rand.uniform(1,6))) + "," + party[c3] + "|" + str(math.floor(rand.uniform(1,6))) + ";" + party[c1] + "\n")
