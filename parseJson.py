import json, requests
import subprocess


def ENCODErequest(url):

	HEADERS = {'accept': 'application/json'}
	URL = url
	response = requests.get(URL, headers=HEADERS)
	responseJson = response.json()
	return responseJson


def getAccession(accessionID):

	print '======================\nrequesting: '+accessionID
	url = "https://www.encodeproject.org/experiments/"+accessionID+"/?frame=embeded"
	accessionJson = ENCODErequest(url)
	return accessionJson


def testReplicates(fileObject):

	if 'replicate' in fileObject:
		return str(fileObject['replicate']['biological_replicate_number'])+','+str(fileObject['replicate']['technical_replicate_number'])
	else:
		return '0,0'


def downloadPeakFiles(fileObject):

	download = {}
	href = fileObject['href']
	tmp = href.split('/')
	# fileName = tmp[len(tmp)-1]
	submittedFileName = fileObject['submitted_file_name']
	fileFormat = fileObject['file_format']
	outputType = fileObject['output_type']
	# print outputType

	if 'peaks' in outputType:
		if (('.bigbed' not in href) and ('.bigBed' not in href)) or ('bed_broadPeak' in fileFormat) or ('bed_narrowPeak' in fileFormat):
			print 'downloading: '+href
			download[(fileObject['accession'])] = testReplicates(fileObject)
			print download
			args = 'https://www.encodeproject.org'+href
			# p = subprocess.Popen('wget '+args, shell=True)
			# p.wait()

	elif 'UniformlyProcessedPeakCalls' in outputType:
		print 'downloading: '+href
		download[(fileObject['accession'])] = testReplicates(fileObject)
		print download
		args = 'https://www.encodeproject.org'+href
		# p = subprocess.Popen('wget '+args, shell=True)
		# p.wait()

	else: 
		print 'no peaks files found for '+fileObject['accession']

	return download


def extractMetadata():




	return




json_data=open('ENCODE_ChIPseq_all_20141010.json')
data = json.load(json_data)
json_data.close()
output = open('ENCODE_ChIPseq_all_20141010_metadata.csv','w')

print len(data['@graph'])


metaData = {}
for ele in data['@graph']:    # per experiment
	accessionJson = getAccession(ele['accession'])
	peakFileMap = {}
	print 'processing: '+ele['accession']
	print str(len(accessionJson['files']))+' files found.'

	if 'target' in accessionJson:
		antibody = accessionJson['target']['label']
	else: antibody = '-'
	
	if 'biosample_term_name' in accessionJson:
		bioSample = accessionJson['biosample_term_name']
	else: bioSample = '-'
	
	if 'assembly' in accessionJson:
		specie = accessionJson['assembly']
	else: specie = 'unknown'
	
	for everyFile in accessionJson['files']:
		print everyFile['accession']
		metaInfo = downloadPeakFiles(everyFile)
		if metaInfo:
			peakFileMap[everyFile['accession']] = metaInfo[everyFile['accession']]
		# metaData[everyFile['accession']] = extractFileMeta(everyFile)


	for downloadedFile in peakFileMap:
		metaData[downloadedFile] = antibody+','+bioSample+','+specie+','+accessionJson['accession']+','+peakFileMap[downloadedFile]
		print peakFileMap[downloadedFile]


for ele in metaData:
	output.write(ele+','+metaData[ele]+'\n')
output.close()
