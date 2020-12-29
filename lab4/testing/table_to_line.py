names = ['e', 'pi', 'sqrt2']

for name in names:
    table = open(name + '_table.txt')
    output = open(name + '.txt', mode='w')
    for line in table:
        a = line.split(' ')
        if len(a) > 1:
            output.write(a[1][:-1])
        else:
            break
    output.close()
    table.close()
