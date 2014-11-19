import shutil
import os


def readFile(inFile):

	with open(inFile, 'r') as inputs:
		lines = inputs.read().split('\n')
		if lines[len(lines)-1]:
			pass
		else:
			lines.pop()
	return lines



# read metadata table
metadata = {}
lines = readFile('ENCODE_ChIPseq_all_20141010_metadata.csv')
for line in lines:
	words = line.split(',')
	metadata[words[0].strip()] = tuple(words[1:])  # key is dataset ID
print metadata['ENCFF002CUM']

# rename files to TF_tissue
for files in os.listdir('peakFile/'):
	if '.bed' in files:
		tmp = files.split('.')
		ID = tmp[0].strip()
		metaName = '_'.join(metadata[ID])+'_'+'.'.join(tmp[1:])
		print ID+': '+metaName
		shutil.copyfile('peakFile/'+files, 'peakFile/metaFile/'+metaName)




# split human and mouse tissues - for mapping purpose 
for files in os.listdir('peakFile/metaFile/'):
	if 'mm9' in files:
		shutil.move('peakFile/metaFile/'+files, 'peakFile/metaFile/mouse/'+files)
	elif 'hg19' in files:
		shutil.move('peakFile/metaFile/'+files, 'peakFile/metaFile/human/'+files)

