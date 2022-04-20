from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseNotFound, JsonResponse

from django.contrib.staticfiles import finders

import firebase_admin
from firebase_admin import credentials as adminCredentials, auth as adminAuth

import pyrebase

import json
import re

from collections import OrderedDict

import base64

cred = adminCredentials.Certificate(finders.find(
    'key/top-50-9951b-firebase-adminsdk-6n5a9-5a5dfe7f4d.json'))
firebase_admin.initialize_app(cred)

config = {
    "apiKey": "AIzaSyBRRqudJsKGAtpUwkecr1kQu2wFOyRMGZY",
    "authDomain": "top-50-9951b.appspot.com",
    "databaseURL": "https://top-50-9951b.firebaseio.com/",
    "storageBucket": "top-50-9951b.appspot.com",
    "serviceAccount": {
        "type": "service_account",
        "project_id": "top-50-9951b",
        "private_key_id": "5a5dfe7f4d8a041537d9a644fd3d148d364d27b0",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYj6QK44SYwhZu\nOwmQnWiBZ7ydDN49rogBr3F5/BaJKmk9q9WhjWciKA0u5j5a5vGLvYN2EmQbnCMe\nxbTfTm5qGpizjXxuKL3/OtmAPJ0jMfA2faRdDMm9aKZsDDtjG5PIiMps8QCMhdzq\nQuYltlxYddnzCkPqf2YP7gPtDNSmv7DbC7gd7x1yDvu5N4+IFtOUF6EhJnDoXp8e\ng6LuKNB4voo8FY1vdetqNPvhk7I6hPlCSWI6M5+y9IRb9FyFnfxwmKk0SNsjqJuF\nuy8WaE5ASZ/nFDcpp68T7yyf/mfo7pxPrrk3uJIuqZlxN4aG0usB3jvmk9LI1uCP\nKXUcILUdAgMBAAECggEABcJWZPrVxJZ/FkYwRD9M9KWf3yXfNeDizX18ASjdCOyK\n8IMOG30lCYsNhFm4vOG++JF85vYmxUwNn1nDTDK/xE2guhZ7PgVXtszX7RlcrTlz\nYfs4OzMCYp4Suj1z+HfQCl/vlFE8vhFHl6hS29WYgtZgFearTazlg3BuwYIcfPbs\nm0jZsfDOpBx+v16Gr7IV5hXZiZ6kkQrXB+sOYTI3wfatneOlKuhT8moPgCWUtL83\nEA7iJ9JCCDutnvRmlg5RTv56HuMrGtVF1uxKFPKfG5OXrNyn6yLilSOk2iz//h3v\n4LjkDKFROlHDMnSQxH9EeA1pO4dmZyAGSSFFtEm+mQKBgQDQOYSkInncHi5DK8ND\nn+jwT182MwVEmb4BGOE/qiK/5xVZC3ERVv8iRo/G6nkv2X2zW2oQS0yPvIxx0DJ8\nIJB7MbYYeRSR7hiirvmWwuSFcrm3Kg2upRo6kbzwASjyi1gFB1k3oF19Zwxicumr\nFRzHzUSDeFcau5KF+qanlUxzFwKBgQC7kJ0e3m7Hi24v+mLoIgvIWNIeaalUExYo\nmgKW+klutqkHU7Gezm1iagIjJVlVpLXA6B6gFpg6izw2snSGHcSENtYIalwFiSHN\nxcXWSK40MNOo8zZ3J8E4519acLLhR3Od1jlH5q8eVk8JcPrzIjz4b80eXF+zcc01\n1IFEkdrJ6wKBgBXJPtiRhuCCA+MhTA/iRlQGafbYxb9Uuq2Qtdica4BapEAp0022\nJYGnklmEpONdxSoj8Wf9COitGKC74Nxd5+AL5nqPCJjwKYGz/wdIIvLXexjv/Hh+\na80e/H68EFW4QKBeEXahf8akJoaScWJmFhnNn1KGH877Oyxrek5kb5hHAoGACPGT\nXGZ019UBMw54auM8tpftpP+a0GR8mQEHAJX8rGfPVYcbIBxtwNSXN3/Pa7MH66Pl\n2fJZ3ejHvT/zKHYA6eEHga04qBbq4rn8fgRHMjvly9eVEEd4AjOeK1zWWsGidLND\nVfddAFBTQnr9rFxElgAWwszaz16sz1VLuK5PxXMCgYEAi7ZFiMJ8IYdjARFa0YKB\nat6NobK+Kza520R+uRFjd+ICVOyOGtsDxxtb0V9nGll11Du82MELgQSwp4pTlOSq\nx2/96a3ay+fDf4b/BJeWdmMWzG28uUktWXorM1itJ4//8UGCW5pymwxPpYxbTeKu\n83jO8l+CqPyvWzfNRyNxLWs=\n-----END PRIVATE KEY-----\n",
        "client_email": "firebase-adminsdk-6n5a9@top-50-9951b.iam.gserviceaccount.com",
        "client_id": "116166562304084917243",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-6n5a9%40top-50-9951b.iam.gserviceaccount.com"
    }
}
firebase = pyrebase.initialize_app(config)

auth = firebase.auth()
db = firebase.database()
storage = firebase.storage()

# Create your views here.


def credentials(request):
    if("user" in request.session):
        return redirect('dashboard')

    if request.method == "POST":
        if 'username' in request.POST:
            if request.POST['username'] == "":
                try:
                    user = auth.sign_in_with_email_and_password(
                        request.POST['email'], request.POST['password'])

                    uid = user['localId']

                    adminCheck = db.child("users/admin/" + uid).get().val()

                    if(adminCheck == None):
                        return JsonResponse({"result": "failure"})

                    user = auth.refresh(user['refreshToken'])

                    user_id = user['idToken']

                    request.session['user'] = user_id
                    request.session['uid'] = uid

                    return JsonResponse({"result": "success"})
                except Exception:
                    return JsonResponse({"result": "failure"})
            else:
                try:
                    user = auth.create_user_with_email_and_password(
                        request.POST['email'], request.POST['password'])

                    user_id = user['idToken']
                    uid = user['localId']

                    request.session['user'] = user_id
                    request.session['uid'] = uid

                    db.child("users/admin/" + uid).set({
                        "name": request.POST['username'],
                        "email": request.POST['email']
                    })

                    return JsonResponse({"result": "success"})
                except Exception as e:
                    print(e)
                    return JsonResponse({"result": "failure"})
        elif 'recoveryEmail' in request.POST:
            try:
                auth.send_password_reset_email(request.POST['recoveryEmail'])

                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})

    return render(request, "credentials.html")


def dashboard(request):
    if("user" not in request.session):
        return redirect("credentials")

    uid = request.session['uid']

    user = db.child("users/admin/" + uid).get().val()

    return render(request, "dashboard.html", {"user": user})


def myDash(request):
    uid = request.session['uid']

    user = dict(db.child("users/admin/" + uid).get().val())

    if request.method == "POST":
        if request.POST["type"] == "profileUpdate":
            try:
                if request.POST["isNameUpdated"] == "true":
                    adminAuth.update_user(
                        uid, display_name=request.POST["username"])
                    db.child("users/admin/" + uid +
                             "/name").set(request.POST['username'])

                if request.POST["isEmailUpdated"] == "true":
                    adminAuth.update_user(
                        uid, email=request.POST["email"])
                    db.child("users/admin/" + uid +
                             "/email").set(request.POST['email'])

                if request.POST["isPasswordUpdated"] == "true":
                    adminAuth.update_user(
                        uid, password=request.POST["password"])

                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})
        elif request.POST["type"] == "validatePassword":
            try:
                userCheck = auth.sign_in_with_email_and_password(
                    request.POST["email"], request.POST["password"])
                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})
        elif request.POST["type"] == "addCategory":
            try:
                id = request.POST["name"].lower()

                storage.child(
                    "content/categories/" + id + ".jpg").put(base64.b64decode(str(request.POST["icon"])), request.session["user"])
                imgURL = storage.child(
                    "content/categories/" + id + ".jpg").get_url(request.session["user"])

                categoryMap = {
                    "name": request.POST["name"].upper(),
                    "color": request.POST["color"],
                    "imgURL": imgURL
                }

                db.child("content/categories/" + id).set(categoryMap)

                categoryMap["id"] = id

                return JsonResponse({"result": "success", "category": categoryMap})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})
        elif request.POST["type"] == "deleteCategory":
            try:
                print(request.POST["category_id"])

                db.child("content/categories/" +
                         request.POST["category_id"]).remove()

                categoryPosts = None

                try:
                    categoryPosts = db.child("content/posts").order_by_child(
                        "category").equal_to(request.POST["category_id"]).get().val()
                except Exception:
                    pass

                categoryPosts = list(
                    dict(categoryPosts).keys()) if categoryPosts != None else []

                updateMap = {}
                for postID in categoryPosts:
                    updateMap[postID] = None

                if len(updateMap.keys()) > 0:
                    db.child("content/posts").update(updateMap)

                return JsonResponse({"result": "success"})
            except Exception as e:
                return JsonResponse({"result": "failure"})

    categories = db.child("content/categories").get().val()
    categories = dict(categories) if categories != None else {}

    return render(request, "myDash.html", {"user": user, "categories": categories})


def categoryDash(request, category_id=""):
    if request.method == "POST":
        if request.POST["reqType"] == "add":
            try:
                newTags = json.loads(
                    request.POST["newTags"]) if "newTags" in request.POST else []
                oldTags = json.loads(
                    request.POST["oldTags"]) if "oldTags" in request.POST else []

                tagsForDeletion = list()
                tagsForAddition = list()

                for tag in oldTags:
                    if tag not in newTags:
                        tagsForDeletion.append(tag)

                for tag in newTags:
                    if tag not in oldTags:
                        tagsForAddition.append(tag)

                for tag in tagsForDeletion:
                    tagCurrentCount = db.child("tags/count/" + tag).get().val()
                    tagCurrentCount = 1 if tagCurrentCount == None else tagCurrentCount
                    db.child("tags/count/" + tag).set(None if tagCurrentCount -
                                                      1 == 0 else tagCurrentCount - 1)

                    db.child("tags/postsAgainstTag/" + tag +
                             "/" + request.POST["key"]).remove()

                for tag in tagsForAddition:
                    tagCurrentCount = db.child("tags/count/" + tag).get().val()
                    tagCurrentCount = 0 if tagCurrentCount == None else tagCurrentCount
                    db.child("tags/count/" + tag).set(tagCurrentCount + 1)

                    db.child("tags/postsAgainstTag/" + tag + "/" +
                             request.POST["key"]).set('postID')

                newTagsMap = {tag: 'tagVal' for tag in newTags}

                data = {
                    "comments": 0,
                    "likes": 0,
                    "type": request.POST["type"],
                    "name": request.POST["name"],
                    "category": category_id,
                    "link": request.POST["link"],
                    "minLink": request.POST["minLink"],
                    "text": str(request.POST["text"]),
                    "timestamp": request.POST["timestamp"],
                    "tags": newTagsMap,
                }

                searchDomain = request.POST["name"] + \
                    " " + request.POST["textNoTags"]
                textWords = searchDomain.strip().split(" ")

                for tag in newTags:
                    textWords.append(tag.lower())

                data["words"] = {}
                wordsMap = {}
                for word in textWords:
                    if(re.fullmatch(r"[-0-9_a-z]+", word.lower())):
                        data["words"][word.lower()] = True
                        db.child("words/" + word.lower() + "/" +
                                 request.POST["key"]).set("postID")

                db.child('content/posts/' +
                         request.POST["key"]).update(data)

                return JsonResponse({"result": "success", "postKey": request.POST["key"], "post": {"type": request.POST["type"],
                                                                                                   "name": request.POST["name"],
                                                                                                   "category": category_id,
                                                                                                   "link": request.POST["link"],
                                                                                                   "minLink": request.POST["minLink"],
                                                                                                   "text": str(request.POST["text"]),
                                                                                                   "timestamp": request.POST["timestamp"],
                                                                                                   "tags": list(json.loads(request.POST["newTags"])) if "newTags" in request.POST else []}})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})
        elif request.POST["reqType"] == "delete":
            try:
                print(category_id)
                db.child('content/posts/' + request.POST["key"]).remove()

                return JsonResponse({"result": "success", "categoryID": category_id, "postKey": request.POST["key"]})
            except:
                return JsonResponse({"result": "failure"})

    categoryPostsMap = {}
    categoryPosts = None
    try:
        categoryPosts = db.child(
            "content/posts").order_by_child("category").equal_to(category_id).get().val()
    except Exception:
        pass

    if categoryPosts != None:
        categoryPosts = dict(categoryPosts)
        for categoryID in categoryPosts:
            if('tags' not in categoryPosts[categoryID]):
                categoryPosts[categoryID]['tags'] = []
            else:
                categoryPosts[categoryID]['tags'] = list(
                    categoryPosts[categoryID]['tags'].keys())

        categoryPostsMap = OrderedDict(sorted(
            categoryPosts.items(), key=lambda post: post[1]['timestamp']))

    words = db.child("words").shallow().get().val()
    words = [] if words == None else list(words)

    return render(request, "categoryDash.html", {"categoryID": category_id, "categoryPosts": json.dumps(categoryPostsMap), "words": words})


def postPreview(request):
    return render(request, "postPreview.html")


def logout(request):
    request.session.pop('user', None)
    request.session.pop('uid', None)

    return redirect("credentials")
