import os
from collections import defaultdict 
import shutil

def readFile(inFile):

	with open(inFile, 'r') as inputs:
		lines = inputs.read().split('\n')
		if lines[len(lines)-1]:
			pass
		else:
			lines.pop()
	return lines


def mergeReplicate(rep1, rep2):

	repName = rep1.replace('1_1','merge').replace('2_1','merge').replace('3_1','merge')
	output = open('mergeRep/'+repName,'w')
	rep1map = {}
	rep2map = {}


	line1 = readFile('sortGene/'+rep1)
	line2 = readFile('sortGene/'+rep2)

	for line in line1:
		gene = line.split('\t')
		rep1map[gene[0].strip()] = int(float(gene[1].strip()))

	for line in line2:
		gene = line.split('\t')
		rep2map[gene[0].strip()] = int(float(gene[1].strip()))

	merged = 0
	for ele in rep1map:
		if ele in rep2map:
			dis = (rep1map[ele]+rep2map[ele])/2
			output.write(ele+'\t'+str(dis)+'\n')
			merged += 1

	output.close()
	print str(len(line1))+', '+str(len(line2))+', '+str(merged)

	return


def sliceNonRep(nonRep, top):

	lines = readFile('sortGene/'+nonRep)
	output = open('mergeRep/'+nonRep,'w')
	if len(lines) <= top:
		shutil.copyfile('sortGene/'+nonRep, 'mergeRep/'+nonRep)
	else:
		output.write('\n'.join(lines[:top]))
		output.close()

	return



# create dictionary for replicates
fileNameMap = defaultdict(list)
count = 0
for filename in os.listdir('sortGene/'):
	if '0_0' not in filename:
		tmp = filename.split('_')
		fileNameMap[tmp[3]].append(filename)
		count += 1
	else:
		shutil.copyfile('sortGene/'+filename, 'mergeRep/'+filename)
print len(fileNameMap)
print str(count)


# merge replicates if replicate files exist
nonRep = 0
for exp in fileNameMap:
	if len(fileNameMap[exp]) > 1:
		# if len(fileNameMap[exp]) == 3:
		# 	print fileNameMap[exp]
		mergeReplicate(fileNameMap[exp][0], fileNameMap[exp][1])
	else: 
		nonRep += 1
		sliceNonRep(fileNameMap[exp][0], 2000)  # for experiments with no actual replicates, take the top 2000 genes

