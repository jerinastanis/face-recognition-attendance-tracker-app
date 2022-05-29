import sys
import cv2
from PIL import Image,ImageFile
import numpy as np
import face_recognition
import os
from datetime import datetime
from os.path import dirname, join
import io
def findEncodings(images):
    encodeList = []
    for img in images:
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = face_recognition.face_encodings(img)[0]
        encodeList.append(encode)
    return encodeList


def markAttendance(name):
    with open('Attendance.csv', 'r+') as f:
        myDataList = f.readlines()
        nameList = []
        for line in myDataList:
            entry = line.split(',')
            nameList.append(entry[0])
        if name not in nameList:
            now = datetime.now()
            dtString = now.strftime('%H:%M:%S')
            f.writelines(f'\n{name},{dtString}')
def main(data):

    path = join(dirname(__file__), "ImageAttendance")

    images = []
    classNames = []
    myList = os.listdir(path)
    print(myList)
    for cl in myList:
        curImg = cv2.imread(f'{path}/{cl}')
        images.append(curImg)
        classNames.append(os.path.splitext(cl)[0])
        print(classNames)
    encodeListKnown = findEncodings(images)
    currentFrame = 0
    while True:
        frame = Image.open(io.BytesIO(bytes(data)))
        frame = np.array(frame)
        # Convert RGB to BGR
        frame = open_cv_image[:, :, ::-1].copy()
        img = imutils.resize(frame, width=min(300, frame.shape[1]))
        #decoded_data=base64.urlsafe_b64decode(data)
        #np_data = np.fromstring(decoded_data,np.uint8)
        #img=cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
        #var=data.json()
        #imgNp=np.array(bytearray(var.read()), dtype=np.uint8)
        #img=cv2.imdecode(imgNp,-1)

        try:
            imgS = cv2.resize(img, (0, 0), None, 0.25, 0.25)
            imgS = cv2.cvtColor(imgS, cv2.COLOR_BGR2RGB)
        except cv2.error as e:
            print('Invalid frame!')
        facesCurFrame = face_recognition.face_locations(imgS)
        encodesCurFrame = face_recognition.face_encodings(imgS, facesCurFrame)
        for encodeFace, faceLoc in zip(encodesCurFrame, facesCurFrame):
            matches = face_recognition.compare_faces(encodeListKnown, encodeFace)
            faceDis = face_recognition.face_distance(encodeListKnown, encodeFace)
            # print(faceDis)
            matchIndex = np.argmin(faceDis)
            if faceDis[matchIndex] < 0.50:
                name = classNames[matchIndex].upper()
                markAttendance(name)
                print(name, '-ur attendance is marked')
                sys.exit()

            else:
                name = 'Unknown'

            # print(name)
            y1, x2, y2, x1 = faceLoc
            y1, x2, y2, x1 = y1 * 4, x2 * 4, y2 * 4, x1 * 4
            cv2.rectangle(img, (x1, y1), (x2, y2), (0, 255, 0), 2)
            cv2.rectangle(img, (x1, y2 - 35), (x2, y2), (0, 255, 0), cv2.FILLED)
            cv2.putText(img, name, (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1, (255, 255, 255),
                        2)
        cv2.imshow('Webcam', img)
        cv2.waitKey(1)
