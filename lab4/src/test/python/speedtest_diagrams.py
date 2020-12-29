import matplotlib.pyplot as plt
import os
import io

dirname = '..\..\..\speedTests'

for r, _, files in os.walk(dirname):
    print(r, files)
    for file in files:
        with io.open(os.path.join(r, file),
                     mode='r', encoding='utf-8') as f:
            name = f.readline()[:-1]
            xline = f.readline().split('\t')
            print(xline)
            x = xline[1:]
            x[-1] = x[-1][:-1]
            plt.title(name)
            plt.xlabel(xline[0])
            plt.ylabel('Время вычисления, мс')
            for line in f:
                parsed = line.split('\t')
                label = parsed[0]
                y = list(map(float, parsed[1:]))
                plt.plot(x, y, label=label, marker='o')
            plt.legend()
            plt.grid()
            plt.savefig(file.split('.')[0] + '.png')
            plt.clf()
            # plt.show()
