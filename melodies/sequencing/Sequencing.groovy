def sentence = 'all mimsy were the borogoves'

def seq = (sentence.split() as List).collect { w ->
    w.toUpperCase()
}.findAll { w ->
    w.length() <= 3
}.join('\n')

println(seq)

//Using Closure Composition
def split = { s ->
    s.split(' ') as List
}

def capitalize = { ws ->
    ws.collect { it.toUpperCase() }
}

def filter = { ws ->
    ws.findAll { it.length() <= 3 }
}

def join = { ws ->
    ws.join('\n')
}

def seqComposed = join << filter << capitalize << split
println (seqComposed(sentence))

def seqAndThened = split >> capitalize >> filter >> join
println (seqAndThened(sentence))