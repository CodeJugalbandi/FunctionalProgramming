//def l = ['alan', 30, 'spiders']
//def l = ['name': 'alan', 'lat': 30, 'fears': 'spiders']
/*def l = 'Hello'*/
def l = [1, 2, 3]
/*def l = 3*/
switch(l) {
  case Tuple: println 'tuple'
  break
  case 1..8: println ([1, 2])
  break
  case [1, 3]: println ([1, 3])
  break
/*  case List : println 'list'*/
/*  break*/
  case Map: println 'map'
  break
  case 'Hello': println 'Hello'
  break
}
