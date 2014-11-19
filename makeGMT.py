import os


def readFile(inFile):

	with open(inFile, 'r') as inputs:
		lines = inputs.read().split('\n')
		if lines[len(lines)-1]:
			pass
		else:
			lines.pop()
	return lines




output1 = open('ENCODE2014_HM.gmt','w')
output2 = open('notEnoughGeneForGMT_HM.txt','w')
count1 = 0
count2 = 0

for filename in os.listdir('mergeRep/HM/'):
	fm = filename.split('_')
	lines = readFile('mergeRep/HM/'+filename)
	if len(lines) > 100:
		count1 += 1
		output1.write('_'.join(fm[:3])+'\tna')
		for line in lines:
			tmp = line.split('\t')
			output1.write('\t'+tmp[0].strip())
		output1.write('\n')
	else:
		output2.write(filename)
		count2 += 1

output1.close()
output2.close()
print count1
print count2
