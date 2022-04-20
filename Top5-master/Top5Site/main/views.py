import logging
import sys, os

from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseNotFound, JsonResponse

from django.contrib.staticfiles import finders

from urllib.error import HTTPError

import firebase_admin
from firebase_admin import credentials as adminCredentials, auth as adminAuth, exceptions as firebaseExceptions

import math
import time
import random
import pyrebase

import json
from urllib.parse import unquote

# Get an instance of a logger
logger = logging.getLogger(__name__)


if not firebase_admin._apps:
    cred = adminCredentials.Certificate(finders.find('site/key/top-50-9951b-firebase-adminsdk-6n5a9-5a5dfe7f4d.json'))
    firebase_admin.initialize_app(cred)

config = {
    "apiKey": "AIzaSyBRRqudJsKGAtpUwkecr1kQu2wFOyRMGZY",
    "authDomain": "top-50-9951b.appspot.com",
    "databaseURL": "https://top-50-9951b.firebaseio.com",
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

loggedInUserCategoricalPosts = []

engAndPersianStaticText = {
    'side_menu_options_home_en': 'Home',
    'side_menu_options_home_fa': 'خانه',
    'side_menu_options_categories_en': 'Categories',
    'side_menu_options_categories_fa': 'دسته‌بندی‌ها',
    'side_menu_options_my_profile_en': 'My Profile',
    'side_menu_options_my_profile_fa': 'مشخصات من',
    'side_menu_options_browse_en': 'Browse',
    'side_menu_options_browse_fa': 'جستجو در اینترنت',
    'side_menu_options_saved_posts_en': 'Saved Posts',
    'side_menu_options_saved_posts_fa': 'پست‌های ذخیره شده',
    'side_menu_options_log_out_en': 'Log Out',
    'side_menu_options_log_out_fa': 'خروج از سیستم',

    'search_placeholder_en': 'Enter Keywords',
    'search_placeholder_fa': 'کلیدواژه‌ها را اینجا وارد کنید',

    'your_fav_recom_site_en': 'YOUR FAVORITE RECOMMENDATON WEBSITE',
    'your_fav_recom_site_fa': 'وب سایت پیشنهادات مورد علاقه شما',

    'search_stuff_you_like_en': 'Search stuff you like...',
    'search_stuff_you_like_fa': '...چیزهایی را که دوست دارید، جستجو کنید',

    'search_en': 'Search',
    'search_fa': 'جستجو',

    'we_have_content_for_the_following_en': 'We have content for the following categories',
    'we_have_content_for_the_following_fa': 'ما برای دسته‌بندی‌های زیر، محتوا داریم',

    'related_content_to_following_categories_en': 'Top5 has content related to the following categories',
    'related_content_to_following_categories_fa': 'Top5 محتوایی مرتبط با دسته‌بندی‌های زیر دارد',

    'comment_input_placeholder_en': 'Enter your comment...',
    'comment_input_placeholder_fa': '...نظر خود را وارد کنید',
    'comment_input_anon_placeholder_en': 'Login to comment...',
    'comment_input_anon_placeholder_fa': '...برای اظهار نظر وارد شوید',

    'comment_post_en': 'Post',
    'comment_post_fa': 'پست',

    'video_en': 'Video',
    'video_fa': 'ویدئو',

    'saved_from_en': 'Saved from',
    'saved_from_fa': 'ذخیره شده از',

    'browse_heading_popular_en': 'POPULAR',
    'browse_heading_popular_fa': 'محبوب',

    'profile_heading_user_profile_en': 'User Profile',
    'profile_heading_user_profile_fa': 'مشخصات کاربر',
    'profile_option_edit_information_en': 'Edit Information',
    'profile_option_edit_information_fa': 'ویرایش اطلاعات',
    'profile_sub_heading_interests_en': 'Your interests',
    'profile_sub_heading_interests_fa': 'علایق شما',
    'profile_heading_filters_en': 'Filters',
    'profile_heading_filters_fa': 'فیلترها',
    'profile_filter_options_made_for_you_en': 'MADE FOR YOU',
    'profile_filter_options_made_for_you_fa': 'تهیه شده برای شما',
    'profile_filter_options_may_also_like_en': 'YOU MAY ALSO LIKE',
    'profile_filter_options_may_also_like_fa': 'ممکن است این مطالب را هم دوست داشته باشید',
    'profile_filter_options_recent_en': 'RECENTLY VIEWED',
    'profile_filter_options_recent_fa': 'به تازگی مشاهده شده',
    'profile_filter_options_new_en': 'NEW',
    'profile_filter_options_new_fa': 'جدید',

    'saved_heading_all_en': 'ALL',
    'saved_heading_all_fa': 'همه',

    'tags_posts_by_tag_en': 'Posts By Tag',
    'tags_posts_by_tag_fa': 'ارسال توسط برچسب',

    'like_en': 'like',
    'like_fa': 'پسندیدن',
    'likes_en': 'likes',
    'likes_fa': 'دوست دارد',

    'comment_en': 'comment',
    'comment_fa': 'اظهار نظر',
    'comments_en': 'comments',
    'comments_fa': 'نظرات',

    'view_more_en': 'View More',
    'view_more_fa': 'بیشتر ببینید',
    'view_prev_en': 'View Prev',
    'view_prev_fa': 'مشاهده قبلی',

    'no_categories_available_en': 'No Categories Available',
    'no_categories_available_fa': 'هیچ دسته بندی موجود نیست',

    'no_posts_available_en': 'No Posts Available',
    'no_posts_available_fa': 'هیچ پستی موجود نیست',

    'no_search_results_heading_en':'No results containing all your search terms were found.',
    'no_search_results_heading_fa':'No results containing all your search terms were found.',

    'no_search_results_en': 'Your search term did not match any posts',
    'no_search_results_fa': 'Your search term did not match any posts',

    'no_search_suggestions_en': 'Suggestions',
    'no_search_suggestions_fa': 'Suggestions',


    'search_suggestions_one_en':'- Make sure that all words are spelled correctly.',
    'search_suggestions_one_fa':'- Make sure that all words are spelled correctly.',

    'search_suggestions_two_en':'- Try different keywords.',
    'search_suggestions_two_fa':'- Try different keywords.',

    'search_suggestions_three_en':'- Try more general keywords.',
    'search_suggestions_three_fa':'- Try more general keywords.',

    'no_saved_posts_en': 'No Saved Posts',
    'no_saved_posts_fa': 'بدون ارسال ذخیره شده',

    'loading_en': 'Loading...',
    'loading_fa': '…در حال بارگذاری',

    'page_en': 'Page',
    'page_fa': 'صفحه',

    'comment_deletion_confirmation_heading_en': 'Are you sure?',
    'comment_deletion_confirmation_heading_fa': 'مطمئنی؟',
    'comment_deletion_confirmation_message_en': 'Are you sure you want to delete this comment?',
    'comment_deletion_confirmation_message_fa': 'آیا مطمئن هستید که می خواهید این نظر را حذف کنید؟',
    'comment_deletion_confirmation_option_yes_en': 'YES',
    'comment_deletion_confirmation_option_yes_fa': 'آره',
    'comment_deletion_confirmation_option_no_en': 'NO',
    'comment_deletion_confirmation_option_no_fa': 'نه',

    'footer_copyright_en': '© Copyright 2020 Top 5, All rights reserved',
    'footer_copyright_fa': 'حق کپی 2020 Top5، تمامی حقوق محفوظ است ©',
}

# Create your views here.


def credentials(request):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    if "user" in request.session:
        return redirect('home')

    redirect_to = request.GET.get('next', '')
    if request.method == "POST":
        if 'username' in request.POST:
            if request.POST['username'] == "":
                try:
                    user = auth.sign_in_with_email_and_password(
                        request.POST['email'], request.POST['password'])

                    uid = user['localId']

                    # print(uid)

                    userCheck = db.child(
                        "users/regularUsers/" + uid).get().val()

                    if(userCheck == None):
                        return JsonResponse({"result": "failure"})

                    user = auth.refresh(user['refreshToken'])

                    user_id = user['idToken']

                    request.session['user'] = user_id
                    request.session['uid'] = uid
                    request.session['lang'] = 'en'

                    redirect_to = request.POST.get('redirect_to', '')

                    return JsonResponse({"result": "success", "redirect_to":redirect_to})
                except Exception as e:
                    print(e)
                    return JsonResponse({"result": "failure"})
            else:
                try:
                    try:
                        userCheck = adminAuth.get_user_by_email(
                            request.POST['email'])
                        if userCheck != None:
                            return JsonResponse({"result": "failure", 'userExists': True})
                    except Exception:
                        print()

                    user = auth.create_user_with_email_and_password(
                        request.POST['email'], request.POST['password'])

                    user_id = user['idToken']
                    uid = user['localId']

                    request.session['user'] = user_id
                    request.session['uid'] = uid
                    request.session['lang'] = 'en'

                    db.child("users/regularUsers/" + uid).set({
                        "name": request.POST['username'],
                        "email": request.POST['email'],
                        "preferences": {
                            "categories": json.loads(request.POST['selectedCategories'])
                        }
                    })

                    return JsonResponse({"result": "success"})
                except Exception as e:
                    return JsonResponse({"result": "failure"})
        elif 'recoveryEmail' in request.POST:
            try:
                auth.send_password_reset_email(request.POST['recoveryEmail'])

                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})

    categories = list()

    categoriesItems = db.child("content/categories").get().val()
    categoriesItems = {} if categoriesItems == None else categoriesItems
    for key, val in categoriesItems.items():
        val["id"] = key
        categories.append(val)

    # redirect_to = request.GET.get('next', '')
    # if redirect_to:
    #     return redirect(redirect_to) 

    return render(request, "credentials/credentials.html", {"categories": categories,"redirect_to":redirect_to})


def home(request):
    global loggedInUserCategoricalPosts

    loadLimit = 10

    categories = {}

    categoriesItems = db.child("content/categories").get().val()
    categoriesItems = {} if categoriesItems == None else categoriesItems
    for key, val in categoriesItems.items():
        val["id"] = key
        categories[key] = val

    if request.method == "POST":
        if request.POST['type'] == "load":
            page_number = int(request.POST['page_number'])
            # print(f"Page Number {page_number}")
            try:
                if(len(categories) > 0):
                    isLoggedIn = "user" in request.session

                    allPosts = list()
                    categoryIDs = categories.keys()

                    if isLoggedIn:
                        userPrefFilter = db.child(
                            "users/regularUsers/" + request.session['uid'] + "/preferences/filterID").get().val()
                        userPrefFilter = int(
                            userPrefFilter) if userPrefFilter != None else 3

                        userTags = db.child(
                            "users/regularUsers/" + request.session['uid'] + "/tags").get().val()
                        userTags = list() if userTags == None else userTags

                        userRecentlyViewed = db.child(
                            "users/regularUsers/" + request.session['uid'] + "/recentlyViewed").get().val()
                        userRecentlyViewed = {} if userRecentlyViewed == None else {
                            item['category']: item['post'] for item in userRecentlyViewed}

                        userPrefCategories = db.child(
                            "users/regularUsers/" + request.session['uid'] + "/preferences/categories").get().val()
                        
                        userPrefCategories = [] if userPrefCategories == None else userPrefCategories
                        # print(userPrefCategories)
                        if len(loggedInUserCategoricalPosts) == 0:
                            allCategoricalPosts = {}
                            for categoryID, categoryValue in userPrefCategories.items():
                                if categoryID in categoryIDs:
                                    categoricalPosts = {}
                                    try:
                                        categoricalPosts = db.child("content").child("posts").order_by_child(
                                            "category").equal_to(categoryID).get().val()

                                        # logger.info("===Categorical Posts ======")
                                        # logger.info("Category Post Size "+str(len(categoricalPosts)))
                                        
                                        if len(categoricalPosts):
                                            if isinstance(categoricalPosts, dict):                                       
                                                # print("Dict Boss ")
                                                # print(type(categoricalPosts))
                                                allCategoricalPosts.update(
                                                    {k: v for k, v in categoricalPosts.items()})
                                            else:
                                                # print("List Boss")
                                                # print(type(categoricalPosts))
                                                for catPost in categoricalPosts:
                                                    allCategoricalPosts.update(
                                                        {k: v for k, v in catPost.items()})
                                    except IndexError as ie:
                                        exc_type, exc_obj, exc_tb = sys.exc_info()
                
                                        logger.error("Category Log ERROR ...")
                                        logger.error(str(exc_tb.tb_lineno)+":"+str(ie))
                                        continue
                                    # logger.info("===allCategoricalPosts ======")
                                    # logger.info(allCategoricalPosts)

                            loggedInUserCategoricalPosts = sorted(
                                allCategoricalPosts.items(), reverse=True)

                        validPosts = []
                        for currPost in loggedInUserCategoricalPosts:
                            post = dict(currPost[1])
                            postID = currPost[0]
                            post["id"] = postID
                            postTags = post["tags"] if "tags" in post else list(
                            )
                            hasCommonTags = False
                            for postTag in postTags:
                                if postTag in userTags:
                                    hasCommonTags = True
                                    break

                            isRecentlyViewed = postID in userRecentlyViewed.values()
                            hasChance = random.randint(1, 3) % 3 == 0

                            #if (userPrefFilter == 0 and hasCommonTags) or (userPrefFilter == 1 and isRecentlyViewed) or (userPrefFilter == 2 and (hasChance or hasCommonTags)) or (userPrefFilter == 3):
                            validPosts.append(post)

                        startAt = page_number * loadLimit

                        selectedPosts = []
                        if(startAt < len(validPosts)):
                            endAt = startAt + loadLimit
                            if(endAt >= len(validPosts)):
                                endAt = len(validPosts)
                            selectedPosts = validPosts[startAt:endAt]
                        else:
                            print("=== JSON response 5")
                            return JsonResponse({"result": "success", "posts": []})

                        for post in selectedPosts:
                            post["category"] = categories[post["category"]]

                            if post['type'] == "article" and post['text'].find("<img") > -1:
                                post['link'] = post['text'][post['text'].find(
                                "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]

                            isLiked = db.child(
                                "likes/" + post['id'] + "/" + request.session['uid']).get().val() != None
                            post["isLiked"] = 1 if isLiked else 0

                            likeStr = "You and Others Liked this." if isLiked else ""
                            likes = db.child("likes/" + post['id']).get().val()
                            if likes != None:
                                likes = list(dict(likes).keys())
                                post["likesCount"] = len(likes)
                                likesCount = len(likes) + \
                                    (-1 if isLiked else 0)
                                limit = 2 if isLiked else 3

                                namedUsers = []
                                for i in range(0, limit):
                                    if i < len(likes) and likes[i] != request.session['uid']:
                                        namedUsers.append(
                                            db.child("users/regularUsers/" + likes[i] + "/name").get().val())

                                if len(namedUsers) == limit or likesCount - len(namedUsers) <= 0:
                                    remainingLikes = likesCount - \
                                        len(namedUsers)

                                    for i in range(0, len(namedUsers)):
                                        if i != 0 and i == len(namedUsers) - 1 and remainingLikes == 0:
                                            likeStr += " and "
                                        else:
                                            if not (remainingLikes > 0 and i == len(namedUsers) - 1) and len(likeStr) > 0:
                                                likeStr += " , "

                                        likeStr += namedUsers[i]

                                    likeStr += " and " + str(remainingLikes) + \
                                        " other like this" if remainingLikes > 0 else " like this"
                                elif len(namedUsers) == 0:
                                    likeStr = "You liked this"

                                post["likeStr"] = likeStr

                            else:
                                post["likeStr"] = "Be the first to like this"

                            isSaved = db.child(
                                "users/regularUsers/" + request.session['uid'] + "/saved/" + post['id']).get().val() != None
                            post["isSaved"] = 1 if isSaved else 0

                            # comments = list()
                            # commentItems = db.child(
                            #     "comments/" + post['id']).get().val()
                            # if commentItems != None:
                            #     for key, val in commentItems.items():
                            #         val["id"] = key

                            #         val["username"] = "You" if val["userID"] == request.session['uid'] else db.child(
                            #             "users/regularUsers/" + val["userID"] + "/name").get().val()

                            #         comments.append(val)

                            # post["allComments"] = comments

                            allPosts.append(post)
                    else:
                        posts = {}

                        postIDs = db.child(
                            "content/posts").shallow().get().val()

                        startAt = page_number * loadLimit

                        selectedPostIds = []
                        if(postIDs != None and startAt < len(postIDs)):
                            postIDs = list(sorted(postIDs, reverse=True))
                            endAt = startAt + loadLimit
                            if(endAt >= len(postIDs)):
                                endAt = len(postIDs)
                            selectedPostIds = postIDs[startAt:endAt]
                        else:
                            # print("=== JSON response 4")
                            return JsonResponse({"result": "success", "posts": []})

                        for postID in selectedPostIds:
                            posts[postID] = dict(
                                db.child("content/posts/" + postID).get().val())

                        for postID in posts:
                            post = posts[postID]
                            post["id"] = postID
                            post["category"] = categories[post["category"]]

                            if post['type'] == "article" and post['text'].find("<img") > -1:
                                post['link'] = post['text'][post['text'].find(
                                "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]

                            post["isLiked"] = 0

                            likeStr = ""
                            likes = db.child("likes/" + postID).get().val()
                            if likes != None:
                                likes = list(dict(likes).keys())
                                post["likesCount"] = len(likes)
                                likesCount = len(likes)
                                limit = 3

                                namedUsers = []
                                for i in range(0, limit):
                                    if i < len(likes):
                                        namedUsers.append(
                                            db.child("users/regularUsers/" + likes[i] + "/name").get().val())

                                if len(namedUsers) == limit or likesCount - len(namedUsers) <= 0:
                                    remainingLikes = likesCount - \
                                        len(namedUsers)

                                    for i in range(0, len(namedUsers)):
                                        if i != 0 and i == len(namedUsers) - 1 and remainingLikes == 0:
                                            likeStr += " and "
                                        else:
                                            if not (remainingLikes > 0 and i == len(namedUsers) - 1) and len(likeStr) > 0:
                                                likeStr += ", "

                                        likeStr += namedUsers[i]

                                    likeStr += " and " + str(remainingLikes) + \
                                        " other like this" if remainingLikes > 0 else " like this"
                                elif len(namedUsers) == 0:
                                    likeStr = "Be the first to like this"

                                post["likeStr"] = likeStr

                            else:
                                post["likeStr"] = "Be the first to like this"

                            # comments = list()
                            # commentItems = db.child(
                            #     "comments/" + postID).get().val()
                            # if commentItems != None:
                            #     for key, val in commentItems.items():
                            #         val["id"] = key

                            #         val["username"] = db.child(
                            #             "users/regularUsers/" + val["userID"] + "/name").get().val()

                            #         comments.append(val)

                            # post["allComments"] = comments

                            allPosts.append(post)
                    # print("=== JSON response 3")
                    return JsonResponse({"result": "success", "posts": allPosts})
                else:
                    # print("=== JSON response 2")
                    return JsonResponse({"result": "success", "posts": []})
            except Exception as e:
                exc_type, exc_obj, exc_tb = sys.exc_info()
                
                logger.error("Home Posts ERROR ...")
                logger.error(str(exc_tb.tb_lineno)+":"+str(e))

                return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "like":
            try:
                currLikeCount = db.child(
                    "content/posts/" + request.POST["postID"] + "/likes").get().val()
                currLikeCount = 0 if currLikeCount == None else currLikeCount


                if request.POST["isLike"] == "true":
                    currLikeCount += 1
                else:
                    if currLikeCount > 0:
                        currLikeCount -= 1

                db.child("content/posts/" +
                         request.POST["postID"] + "/likes").set(currLikeCount)

                if request.POST["isLike"] == "true":
                    db.child(
                        "likes/" + request.POST["postID"] + "/" + request.session['uid']).set("userID")
                else:
                    db.child(
                        "likes/" + request.POST["postID"] + "/" + request.session['uid']).remove()

                return JsonResponse({"result": "success", "likes": currLikeCount})
            except:
                return JsonResponse({"result": "failure"})
        # elif request.POST['type'] == "comment":
        #     try:
        #         currCommentsCount = db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").get().val()
        #         currCommentsCount = 0 if currCommentsCount == None else currCommentsCount

        #         db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").set(currCommentsCount + 1)

        #         comment = db.child("comments/" + request.POST["postID"]).push({
        #             "comment": request.POST["comment"],
        #             "timestamp": int(request.POST["timestamp"]),
        #             "userID": request.session['uid']
        #         })

        #         commentID = comment['name']

        #         return JsonResponse({"result": "success", "commentID": commentID, "comments": currCommentsCount + 1})
        #     except Exception as e:
        #         print(e)
        #         return JsonResponse({"result": "failure"})
        # elif request.POST['type'] == "delete_comment":
        #     try:
        #         currCommentsCount = db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").get().val()
        #         currCommentsCount = 1 if currCommentsCount == None else currCommentsCount

        #         if(currCommentsCount > 0):
        #             db.child(
        #                 "content/posts/" + request.POST["postID"] + "/comments").set(currCommentsCount - 1)

        #         db.child(
        #             "comments/" + request.POST["postID"] + "/" + request.POST["commentID"]).remove()

        #         return JsonResponse({"result": "success", "comments": currCommentsCount - 1})
        #     except Exception as e:
        #         print(e)
        #         return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "save":
            try:
                if request.POST["isSave"] == "true":
                    db.child("users/regularUsers/" + request.session['uid'] + "/saved/" + request.POST["postID"]).set(
                        request.POST["categoryID"])
                else:
                    db.child("users/regularUsers/" +
                             request.session['uid'] + "/saved/" + request.POST["postID"]).remove()

                return JsonResponse({"result": "success"})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})

    recentPosts = []
    lastThreePosts = db.child(
        "content/posts").order_by_key().limit_to_last(3).get().val()

    if lastThreePosts != None:
        lastThreePosts = dict(lastThreePosts)
        for postID in lastThreePosts:
            post = lastThreePosts[postID]
            post['id'] = postID

            if post['type'] == "article" and post['text'].find("<img") > -1:
                post['link'] = post['text'][post['text'].find(
                "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]

            recentPosts.append(post)

        recentPosts = sorted(
            recentPosts, key=lambda post: post['id'], reverse=True)

    return render(request, "site/pages/home_infinite.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "userID": request.session['uid'] if "user" in request.session else "", "categories": categories, "recentPosts": recentPosts, "recentPostsCount": len(recentPosts), "serverTime": int(math.floor(time.time() * 1000))})


def categories(request, category_id='none', page_number=0):
    categories = {}

    categoriesItems = db.child("content/categories").get().val()
    categoriesItems = {} if categoriesItems == None else categoriesItems
    for key, val in categoriesItems.items():
        val["id"] = key
        categories[key] = val

    if category_id != "none":
        if(category_id not in categories):
            return redirect("invalid")

        if request.method == "POST":
            if request.POST['type'] == "load":
                try:
                    loadLimit = 10

                    allPosts = list()

                    posts = {}

                    categoricalPosts = {}

                    try:
                        categoricalPosts = db.child("content/posts").order_by_child(
                            "category").equal_to(category_id).get().val()
                        categoricalPosts = {} if categoricalPosts == None else {
                            k: v for k, v in categoricalPosts.items()}
                    except IndexError as e:
                        categoricalPosts = {}

                    postIDs = sorted(categoricalPosts.keys(), reverse=True)

                    maxPossiblePages = (len(postIDs) % loadLimit) + 1

                    correctedPageNumber = maxPossiblePages - \
                        1 if page_number >= maxPossiblePages else page_number

                    startAt = correctedPageNumber * loadLimit

                    if(startAt >= len(postIDs)):
                        return JsonResponse({"result": "success", "posts": []})

                    hasNext = True

                    if len(postIDs) > 0:
                        if startAt >= len(postIDs):
                            hasNext = False
                            startAt = len(postIDs) - loadLimit + 1
                        elif startAt < 0:
                            startAt = 0

                        for i in range(startAt, startAt + loadLimit):
                            if i < len(postIDs):
                                posts[postIDs[i]] = categoricalPosts[postIDs[i]]

                        for postID in posts:
                            post = posts[postID]
                            post["id"] = postID
                            post["category"] = categories[post["category"]]

                            if post['type'] == "article" and post['text'].find("<img") > -1:
                                post['link'] = post['text'][post['text'].find(
                                "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]

                            isLiked = False if not "user" in request.session else db.child(
                                "likes/" + postID + "/" + request.session['uid']).get().val() != None
                            post["isLiked"] = 1 if isLiked else 0

                            likeStr = "You and Others Liked this" if isLiked else ""
                            likes = db.child("likes/" + postID).get().val()
                            post["likesCount"] = 0
                            if likes != None:
                                likes = list(dict(likes).keys())
                                post["likesCount"] = len(likes)
                                likesCount = len(likes) + \
                                    (-1 if isLiked else 0)
                                limit = 2 if isLiked else 3

                                namedUsers = []
                                for i in range(0, limit):
                                    if i < len(likes):
                                        if "user" in request.session and likes[i] == request.session['uid']:
                                            continue

                                        namedUserName = db.child(
                                            "users/regularUsers/" + likes[i] + "/name").get().val()
                                        if namedUserName != None:
                                            namedUsers.append(
                                                db.child("users/regularUsers/" + likes[i] + "/name").get().val())

                                if len(namedUsers) == limit or likesCount - len(namedUsers) <= 0:
                                    remainingLikes = likesCount - \
                                        len(namedUsers)

                                    for i in range(0, len(namedUsers)):
                                        if i != 0 and i == len(namedUsers) - 1 and remainingLikes == 0:
                                            likeStr += " and "
                                        else:
                                            if not (remainingLikes > 0 and i == len(namedUsers) - 1) and len(likeStr) > 0:
                                                likeStr += ", "

                                        likeStr += namedUsers[i]

                                    likeStr += " and " + str(remainingLikes) + \
                                        " other like this" if remainingLikes > 0 else " like this"
                                elif len(namedUsers) == 0:
                                    likeStr = "You liked this"

                                post["likeStr"] = likeStr

                            else:
                                post["likeStr"] = "Be the first to like this"

                            isSaved = False if not "user" in request.session else db.child(
                                "users/regularUsers/" + request.session['uid'] + "/saved/" + postID).get().val() != None
                            post["isSaved"] = 1 if isSaved else 0

                            # comments = list()
                            # commentItems = db.child(
                            #     "comments/" + postID).get().val()
                            # if commentItems != None:
                            #     for key, val in commentItems.items():
                            #         val["id"] = key

                            #         if "user" in request.session:
                            #             val["username"] = "You" if val["userID"] == request.session['uid'] else db.child(
                            #                 "users/regularUsers/" + val["userID"] + "/name").get().val()
                            #         else:
                            #             val["username"] = db.child(
                            #                 "users/regularUsers/" + val["userID"] + "/name").get().val()

                            #         comments.append(val)

                            # post["allComments"] = comments

                            allPosts.append(post)

                        if len(allPosts) < loadLimit:
                            hasNext = False

                    # allPosts.reverse()

                    return JsonResponse({"result": "success", "posts": allPosts, "pageNum": correctedPageNumber, "pageNumForView": 1 + correctedPageNumber, "hasNext": hasNext, "loadLimit": loadLimit})
                except Exception as e:
                    # print("Posts by category ...")
                    # print(e)
                    exc_type, exc_obj, exc_tb = sys.exc_info()
                
                    logger.error("Posts by category ...")
                    logger.error(str(exc_tb.tb_lineno)+":"+str(e))
                    return JsonResponse({"result": "failure", "posts": list()})
            elif request.POST['type'] == "like":
                try:
                    currLikeCount = db.child(
                        "content/posts/" + request.POST["postID"] + "/likes").get().val()
                    currLikeCount = 0 if currLikeCount == None else currLikeCount

                    if request.POST["isLike"] == "true":
                        currLikeCount += 1
                    else:
                        if currLikeCount > 0:
                            currLikeCount -= 1

                    db.child("content/posts/" +
                             request.POST["postID"] + "/likes").set(currLikeCount)

                    if request.POST["isLike"] == "true":
                        db.child(
                            "likes/" + request.POST["postID"] + "/" + request.session['uid']).set("userID")
                    else:
                        db.child(
                            "likes/" + request.POST["postID"] + "/" + request.session['uid']).remove()

                    return JsonResponse({"result": "success", "likes": currLikeCount})
                except:
                    return JsonResponse({"result": "failure"})
            elif request.POST['type'] == "save":
                try:
                    if request.POST["isSave"] == "true":
                        db.child(
                            "users/regularUsers/" + request.session['uid'] + "/saved/" + request.POST["postID"]).set(category_id)
                    else:
                        db.child("users/regularUsers/" +
                                 request.session['uid'] + "/saved/" + request.POST["postID"]).remove()

                    return JsonResponse({"result": "success"})
                except Exception as e:
                    print(e)
                    return JsonResponse({"result": "failure"})
    # print("=========== CATEGORIES  =========")
    # print(categories)
    return render(request, "site/pages/categories.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "isCategoryPostsPage": category_id != "none", "categoryID": category_id, "pageNumber": page_number, "categories": categories, "serverTime": int(math.floor(time.time() * 1000))})


def post(request, post_title_id):
    post_title_id = unquote(post_title_id)
    postID = post_title_id

    if request.method == "POST":
        if request.POST['type'] == "like":
            try:
                currLikeCount = db.child(
                    "content/posts/" + postID + "/likes").get().val()
                currLikeCount = 0 if currLikeCount == None else currLikeCount

                #TODO:Check if user has liked post already
                hasLiked = False if not "user" in request.session else db.child(
                "likes/" + postID + "/" + request.session['uid']).get().val() != None
                if request.POST["isLike"] == "true" and not hasLiked:
                    currLikeCount += 1
                    if currLikeCount == 0:
                        likeStr = "Be the first to like this."
                    elif currLikeCount ==1:
                        likeStr = "You liked this."
                    elif currLikeCount > 0:
                        likeStr = "You and Others liked this"
                    else:
                        likeStr = ""

                    db.child(
                        "likes/" + postID + "/" + request.session['uid']).set("userID")

                if request.POST["isLike"] == "false" and hasLiked:
                    if currLikeCount > 0:
                        currLikeCount -= 1
                        likeStr = "You disliked this."

                        db.child(
                        "likes/" + postID + "/" + request.session['uid']).remove()
                    
                db.child("content/posts/" +
                         postID + "/likes").set(currLikeCount)
                # likeStr
                return JsonResponse({"result": "success", "likes": currLikeCount, "likesCount":currLikeCount, "likeStrWR":likeStr})
            except:
                return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "comment":
            try:
                currCommentsCount = db.child(
                    "content/posts/" + postID + "/comments").get().val()
                currCommentsCount = 0 if currCommentsCount == None else currCommentsCount

                db.child(
                    "content/posts/" + postID + "/comments").set(currCommentsCount + 1)

                comment = db.child("comments/" + postID).push({
                    "comment": request.POST["comment"],
                    "timestamp": int(math.floor(time.time() * 1000)),
                    "userID": request.session['uid']
                })

                commentID = comment['name']

                return JsonResponse({"result": "success", "commentID": commentID, "comments": currCommentsCount + 1})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "delete_comment":
            try:
                currCommentsCount = db.child(
                    "content/posts/" + postID + "/comments").get().val()
                currCommentsCount = 1 if currCommentsCount == None else currCommentsCount

                if(currCommentsCount > 0):
                    db.child(
                        "content/posts/" + postID + "/comments").set(currCommentsCount - 1)

                db.child(
                    "comments/" + postID + "/" + request.POST["commentID"]).remove()

                return JsonResponse({"result": "success", "comments": currCommentsCount - 1})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "save":
            try:
                if request.POST["isSave"] == "true":
                    db.child("users/regularUsers/" + request.session['uid'] + "/saved/" + postID).set(
                        request.POST["categoryID"])
                else:
                    db.child("users/regularUsers/" +
                             request.session['uid'] + "/saved/" + postID).remove()

                return JsonResponse({"result": "success"})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure"})

    post = db.child("content/posts/" + postID).get().val()
    if post != None:
        categories = {}

        categoriesItems = db.child("content/categories").get().val()
        for key, val in categoriesItems.items():
            val["id"] = key
            categories[key] = val
        categoryIDs = categories.keys()

        post = dict(post)
        post["id"] = postID
        post["category"] = categories[post["category"]]

        isLiked = False if not "user" in request.session else db.child(
            "likes/" + postID + "/" + request.session['uid']).get().val() != None
        post["isLiked"] = 1 if isLiked else 0

        likeStr = "You liked this" if isLiked else ""
        likes = db.child("likes/" + postID).get().val()
        post["likesCount"] = 0
        if likes != None:
            likes = list(dict(likes).keys())
            # print("Like Count is  "+str(len(likes)))
            post["likesCount"] = len(likes)
            likesCount = len(likes) + (-1 if isLiked else 0)
            limit = 2 if isLiked else 3


            namedUsers = []
            for i in range(0, limit):
                if i < len(likes):
                    if "user" in request.session and likes[i] == request.session['uid']:
                        continue

                    namedUserName = db.child(
                        "users/regularUsers/" + likes[i] + "/name").get().val()
                    # print("Named User "+namedUserName)
                    if namedUserName != None:
                        namedUsers.append(
                            db.child("users/regularUsers/" + likes[i] + "/name").get().val())
                    
            if len(namedUsers) == limit or likesCount - len(namedUsers) <= 0:
                remainingLikes = likesCount - len(namedUsers)

                for i in range(0, len(namedUsers)):
                    if i != 0 and i == len(namedUsers) - 1 and remainingLikes == 0:
                        likeStr += " and "
                    else:
                        #not (remainingLikes > 0 and i == len(namedUsers) - 1) and
                        if not (remainingLikes > 0 and i == len(namedUsers) - 1) and len(likeStr) > 0:
                            likeStr += ", "

                    likeStr += f"{namedUsers[i]}, "

                likeStr += " and " + str(remainingLikes) + \
                    " other like this" if remainingLikes > 0 else " like this"
            elif len(namedUsers) == 0:
                likeStr = "You like this"

            post["likeStr"] = likeStr

        else:
            post["likeStr"] = "Be the first to like this"

        isSaved = False if not "user" in request.session else db.child(
            "users/regularUsers/" + request.session['uid'] + "/saved/" + postID).get().val() != None
        post["isSaved"] = 1 if isSaved else 0

        comments = list()
        commentItems = db.child(
            "comments/" + postID).get().val()
        uid = request.session.get('uid', None)
        if commentItems != None:
            for key, val in commentItems.items():
                val["id"] = key

                val["username"] = "You" if val["userID"] == uid else db.child(
                    "users/regularUsers/" + val["userID"] + "/name").get().val()

                val["profilePhoto"] = db.child("users/regularUsers/" + val["userID"] + "/profilePhoto").get().val()

                comments.append(val)

        post["allComments"] = comments
    else:
        return redirect("invalid")

    userPhoto = ""
    if uid:
        userPhoto = db.child("users/regularUsers/" + uid + "/profilePhoto").get().val()

    return render(request, "site/pages/post.html", {"isLoggedIn": "user" in request.session, "userPhoto": userPhoto, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "userID": request.session['uid'] if "user" in request.session else "", "urlQuery": post_title_id, "post": post, "postMap": json.dumps(dict(post)), "serverTime": int(math.floor(time.time() * 1000))})


def profile(request):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    if not "user" in request.session:
        return redirect('home')

    if request.method == "POST":
        if request.POST['type'] == "filter":
            try:
                db.child("users/regularUsers/" +
                         request.session['uid'] + "/preferences/filterID").set(request.POST['filterID'])

                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "interest":
            try:
                db.child("users/regularUsers/" + request.session['uid'] + "/preferences/categories/" + request.POST['categoryID']).set(
                    None if request.POST['interestOperation'] == "remove" else "categoryID")

                return JsonResponse({"result": "success"})
            except:
                return JsonResponse({"result": "failure"})
        # elif request.POST['type'] == "saveProfile":
        #     try:
        #         db.child("users/regularUsers/" + request.session['uid']).update({
        #             # 'name': request.POST['name'],
        #             # 'email': request.POST['email'],
        #             'gender': request.POST['gender'] if request.POST['gender'] != "" else None,
        #             'description': request.POST['description'] if request.POST['description'] != "" else None
        #         })

        #         return JsonResponse({"result": "success", "info": {'gender': request.POST['gender'], 'description': request.POST['description']}})
        #     except:
        #         return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "langChange":
            request.session['lang'] = 'fa' if request.session['lang'] == 'en' else 'en'
            return JsonResponse({"result": "success"})

    categories = {}

    categoriesItems = db.child("content/categories").get().val()
    categoriesItems = {} if categoriesItems == None else categoriesItems
    for key, val in categoriesItems.items():
        val["id"] = key
        categories[key] = val
    categoryIDs = categories.keys()

    uid = request.session['uid']
    user = db.child("users/regularUsers/" + uid).get().val()

    userPrefFilter = 3
    userPrefCategories = []
    userDetails = {
        'uid': uid,
        'name': 'N/A',
        'email': 'N/A',
        'profilePhoto': ''

        # 'gender': 'N/A',
        # 'description': "N/A"
    }

    if user != None:
        user = dict(user)

        if 'name' in user:
            userDetails['name'] = user['name']

        if 'email' in user:
            userDetails['email'] = user['email']

        if 'profilePhoto' in user:
            userDetails['profilePhoto'] = user['profilePhoto']

        # if 'gender' in user:
        #     userDetails['gender'] = user['gender']

        # if 'description' in user:
        #     userDetails['description'] = user['description']

        userPrefFilter = 3 if 'preferences' not in user else (
            3 if 'filterID' not in user['preferences'] else int(user['preferences']['filterID']))

        userPrefCategories = [] if 'preferences' not in user else ([] if 'categories' not in user['preferences'] else [
                                                                   k for k, v in user['preferences']['categories'].items()])

    interests = list()
    for categoryID in userPrefCategories:
        if categoryID in categoryIDs:
            interests.append(categories[categoryID])



    return render(request, "site/pages/userProfile.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, 'userDetails': userDetails, "categories": categories, "interests": interests, "filterID": userPrefFilter})


def browse(request, searchTerm=""):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    # print("Search Term "+searchTerm)
    #
    # engAndPersianStaticText['no_search_results_en'] = engAndPersianStaticText['no_search_results_en'].replace(
    #     "@searchTerm", searchTerm)
    # engAndPersianStaticText['no_search_results_fa'] = engAndPersianStaticText['no_search_results_fa'].replace(
    #     "@searchTerm", searchTerm)

    if request.method == "POST":
        if request.POST['type'] == "load":
            try:
                loadLimit = 10

                categories = {}
                allPosts = list()

                categoriesItems = db.child("content/categories").get().val()
                categoriesItems = {} if categoriesItems == None else categoriesItems
                for key, val in categoriesItems.items():
                    val["id"] = key
                    categories[key] = val

                if len(categoriesItems) > 0:
                    posts = {}
                    words = str(request.POST["searchTerm"]
                                ).lower().strip().split()
                    postIDs = {}
                    for word in words:
                        wordPostIDs = db.child("words").order_by_key().start_at(word).end_at(word + "\uf8ff").get().val()
                        if wordPostIDs != None:
                            wordPostIDs = dict(wordPostIDs)
                            for wordID in wordPostIDs:
                                for postID in wordPostIDs[wordID].items():
                                    postIDs[postID[0]] = "postID"
                        print("wordPostIDs")
                        print(wordPostIDs)  
                    postIDs = sorted(postIDs)

                    maxPossiblePages = (len(postIDs) % loadLimit) + 1

                    correctedPageNumber = maxPossiblePages - \
                        1 if int(request.POST["pageNumber"]) >= maxPossiblePages else int(
                            request.POST["pageNumber"])

                    startAt = correctedPageNumber * loadLimit

                    hasNext = True

                    if len(postIDs) > 0:
                        if startAt >= len(postIDs):
                            hasNext = False
                            startAt = len(postIDs) - loadLimit + 1
                        elif startAt < 0:
                            startAt = 0

                        for i in range(startAt, startAt + loadLimit):
                            if i < len(postIDs):
                                postMap = db.child(
                                    "content/posts/" + postIDs[i]).get().val()
                                if postMap != None:
                                    posts[postIDs[i]] = dict(postMap)

                        for postID in posts:
                            post = posts[postID]
                            post["id"] = postID
                            post["category"] = categories[post["category"]]

                            isLiked = False if not "user" in request.session else db.child(
                                "likes/" + postID + "/" + request.session['uid']).get().val() != None
                            post["isLiked"] = 1 if isLiked else 0

                            likeStr = "You and Others Liked this." if isLiked else ""
                            likes = db.child("likes/" + postID).get().val()
                            if likes != None:
                                likes = list(dict(likes).keys())
                                likesCount = len(likes) + \
                                    (-1 if isLiked else 0)
                                limit = 2 if isLiked else 3

                                namedUsers = []
                                for i in range(0, limit):
                                    if i < len(likes):
                                        if "user" in request.session and likes[i] == request.session['uid']:
                                            continue

                                        namedUserName = db.child(
                                            "users/regularUsers/" + likes[i] + "/name").get().val()
                                        if namedUserName != None:
                                            namedUsers.append(
                                                db.child("users/regularUsers/" + likes[i] + "/name").get().val())

                                if len(namedUsers) == limit or likesCount - len(namedUsers) <= 0:
                                    remainingLikes = likesCount - \
                                        len(namedUsers)

                                    for i in range(0, len(namedUsers)):
                                        if i != 0 and i == len(namedUsers) - 1 and remainingLikes == 0:
                                            likeStr += " and "
                                        else:
                                            if not (remainingLikes > 0 and i == len(namedUsers) - 1) and len(likeStr) > 0:
                                                likeStr += " , "

                                        likeStr += namedUsers[i]

                                    likeStr += " and " + str(remainingLikes) + \
                                        " other like this" if remainingLikes > 0 else " like this"
                                elif len(namedUsers) == 0:
                                    likeStr = "You liked this"

                                post["likeStr"] = likeStr

                            else:
                                post["likeStr"] = "Be the first to like this"

                            isSaved = False if not "user" in request.session else db.child(
                                "users/regularUsers/" + request.session['uid'] + "/saved/" + postID).get().val() != None
                            post["isSaved"] = 1 if isSaved else 0

                            # comments = list()
                            # commentItems = db.child(
                            #     "comments/" + postID).get().val()
                            # if commentItems != None:
                            #     for key, val in commentItems.items():
                            #         val["id"] = key

                            #         if "user" in request.session:
                            #             val["username"] = "You" if val["userID"] == request.session['uid'] else db.child(
                            #                 "users/regularUsers/" + val["userID"] + "/name").get().val()
                            #         else:
                            #             val["username"] = db.child(
                            #                 "users/regularUsers/" + val["userID"] + "/name").get().val()

                            #         comments.append(val)

                            # post["allComments"] = comments

                            allPosts.append(post)

                        if len(allPosts) < loadLimit:
                            hasNext = False

                    allPosts.reverse()

                    return JsonResponse({"result": "success", "searchTerm": str(request.POST["searchTerm"]), "posts": allPosts, "pageNum": correctedPageNumber, "pageNumForView": 1 + correctedPageNumber, "hasNext": hasNext, "loadLimit": loadLimit})
                else:
                    errorMessage = ""
                    if request.session['lang'] == "en":
                        errorMessage = engAndPersianStaticText['no_search_results_en']
                    else:
                        errorMessage = engAndPersianStaticText['no_search_results_fa']

                    return JsonResponse({"result": "success", "searchTerm": str(request.POST["searchTerm"]), "posts": [], "errorMessage": errorMessage})
            except Exception as e:
                print(e)
                return JsonResponse({"result": "failure", "posts": list(), "errorMessage": str(e)})
        elif request.POST['type'] == "like":
            try:
                currLikeCount = db.child(
                    "content/posts/" + request.POST["postID"] + "/likes").get().val()
                currLikeCount = 0 if currLikeCount == None else currLikeCount

                if request.POST["isLike"] == "true":
                    currLikeCount += 1
                else:
                    if currLikeCount > 0:
                        currLikeCount -= 1
                        
                db.child("content/posts/" +
                         request.POST["postID"] + "/likes").set(currLikeCount)

                if request.POST["isLike"] == "true":
                    db.child(
                        "likes/" + request.POST["postID"] + "/" + request.session['uid']).set("userID")
                else:
                    db.child(
                        "likes/" + request.POST["postID"] + "/" + request.session['uid']).remove()

                return JsonResponse({"result": "success", "likes": currLikeCount})
            except:
                return JsonResponse({"result": "failure"})
        # elif request.POST['type'] == "comment":
        #     try:
        #         currCommentsCount = db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").get().val()
        #         currCommentsCount = 0 if currCommentsCount == None else currCommentsCount

        #         db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").set(currCommentsCount + 1)

        #         comment = db.child("comments/" + request.POST["postID"]).push({
        #             "comment": request.POST["comment"],
        #             "timestamp": int(request.POST["timestamp"]),
        #             "userID": request.session['uid']
        #         })

        #         commentID = comment['name']

        #         return JsonResponse({"result": "success", "commentID": commentID, "comments": currCommentsCount + 1})
        #     except Exception as e:
        #         print(e)
        #         return JsonResponse({"result": "failure"})
        # elif request.POST['type'] == "delete_comment":
        #     try:
        #         currCommentsCount = db.child(
        #             "content/posts/" + request.POST["postID"] + "/comments").get().val()
        #         currCommentsCount = 1 if currCommentsCount == None else currCommentsCount

        #         if(currCommentsCount > 0):
        #             db.child(
        #                 "content/posts/" + request.POST["postID"] + "/comments").set(currCommentsCount - 1)

        #         db.child(
        #             "comments/" + request.POST["postID"] + "/" + request.POST["commentID"]).remove()

        #         print(request.POST["postID"])
        #         print(request.POST["commentID"])

        #         return JsonResponse({"result": "success", "comments": currCommentsCount - 1})
        #     except Exception as e:
        #         print(e)
        #         return JsonResponse({"result": "failure"})
        elif request.POST['type'] == "save":
            try:
                if request.POST["isSave"] == "true":
                    db.child("users/regularUsers/" + request.session['uid'] + "/saved/" + request.POST["postID"]).set(
                        request.POST["categoryID"])
                else:
                    db.child("users/regularUsers/" +
                             request.session['uid'] + "/saved/" + request.POST["postID"]).remove()

                return JsonResponse({"result": "success"})
            except Exception as e:
                # print(e)
                return JsonResponse({"result": "failure"})

    allTagsCount = db.child("tags/count").get().val()
    allTagsCount = [] if allTagsCount == None else dict(allTagsCount)

    if type(allTagsCount) == dict:
        allTagsCount = sorted(allTagsCount.items(),
                              key=lambda x: x[1], reverse=True)

    allTagsCount = allTagsCount[0: 10 if len(
        allTagsCount) >= 10 else len(allTagsCount)]
    allTagsCount = [tag[0] for tag in allTagsCount]

    selectedTags = list()

    for i in range(0, 4 if len(allTagsCount) >= 4 else len(allTagsCount)):
        selectedTags.append(random.choice(allTagsCount))
        allTagsCount.remove(selectedTags[-1])

    selectedTagPosts = {}
    for tag in selectedTags:
        postID = db.child("tags/postsAgainstTag/" +
                          tag).order_by_key().limit_to_first(1).get().val()
        if postID == None:
            continue
        else:
            postID = list(postID.keys())[0]

        post = db.child("content/posts/" + postID).get().val()
        if post != None:
            if post['type'] == "article" and post['text'].find("<img") > -1:
                post['link'] = post['text'][post['text'].find(
                "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]

            selectedTagPosts[tag] = post['link']

    popularPosts = []
    allLikes = db.child("likes").get().val()
    if allLikes != None:
        allLikes = dict(allLikes)
        allLikesCount = {}

        sampledLikes = random.sample(list(allLikes.keys()), 100 if len(
            allLikes) >= 100 else len(allLikes))

        for likeID in sampledLikes:
            allLikesCount[likeID] = len(list(allLikes[likeID].keys()))

        allLikesCount = sorted(allLikesCount.items(),
                               key=lambda x: x[1], reverse=True)

        popularPostIDs = dict(allLikesCount[0: 3 if len(
            allLikesCount) >= 3 else len(allLikesCount)])

        for postID in popularPostIDs:
            post = db.child("content/posts/" + postID).get().val()

            if post != None:
                
                post['id'] = postID
                if post['type'] == "article" and post['text'].find("<img") > -1:
                    post['link'] = post['text'][post['text'].find(
                    "<img src=") + 10: post['text'].find(" alt" if "firebasestorage" in post['text'] else "alt") - 2]   
                popularPosts.append(post)

       

    return render(request, "site/pages/browse.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "userID": request.session['uid'] if "user" in request.session else "", "serverTime": int(math.floor(time.time() * 1000)), "tags": selectedTagPosts, "popularPosts": popularPosts, "searchTerm": searchTerm})


def saved(request, page_number=0):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    if not "user" in request.session:
        return redirect('home')

    loadLimit = 10

    categories = {}
    allPosts = list()

    categoriesItems = db.child("content/categories").get().val()
    categoriesItems = {} if categoriesItems == None else categoriesItems
    for key, val in categoriesItems.items():
        val["id"] = key
        categories[key] = val

    userSavedPostIDs = db.child(
        "users/regularUsers/" + request.session['uid'] + "/saved").get().val()
    userSavedPostIDs = {} if userSavedPostIDs == None else {
        k: v for k, v in userSavedPostIDs.items()}

    tempUserSavedPostIDs = {}
    categoryIDs = categories.keys()
    for postID in userSavedPostIDs:
        if userSavedPostIDs[postID] in categoryIDs:
            tempUserSavedPostIDs[postID] = userSavedPostIDs[postID]

    userSavedPostIDs = tempUserSavedPostIDs

    userSavedPostIDsSorted = sorted(userSavedPostIDs.items(), reverse=True)

    maxPossiblePages = (len(userSavedPostIDsSorted) % loadLimit) + 2

    if page_number >= maxPossiblePages:
        return redirect("invalid")

    correctedPageNumber = maxPossiblePages - \
        1 if page_number >= maxPossiblePages else page_number

    startAt = correctedPageNumber * loadLimit

    hasNext = True

    if len(userSavedPostIDsSorted):
        if startAt >= len(userSavedPostIDsSorted):
            hasNext = False
            startAt = len(userSavedPostIDsSorted) - loadLimit + 1
        elif startAt < 0:
            startAt = 0

        for i in range(startAt, startAt + loadLimit):
            if i < len(userSavedPostIDsSorted):
                postID = userSavedPostIDsSorted[i][0]
                post = db.child("content/posts/" + postID).get().val()
                if post == None:
                    break
                post = dict(post)
                post["id"] = postID
                post["category"] = categories[userSavedPostIDs[postID]]

                allPosts.append(post)

        if len(allPosts) < loadLimit:
            hasNext = False

    return render(request, "site/pages/savedPosts.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "posts": allPosts, "pageNum": correctedPageNumber, "pageNumForView": 1 + correctedPageNumber, "hasNext": hasNext})


def tags(request, tag, page_number=0):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    loadLimit = 10

    categories = {}
    allPosts = list()

    categoriesItems = db.child("content/categories").get().val()
    for key, val in categoriesItems.items():
        val["id"] = key
        categories[key] = val

    tagPostIDs = db.child(
        "tags/postsAgainstTag/" + tag).get().val()
    tagPostIDs = {} if tagPostIDs == None else dict(tagPostIDs.items())

    tagPostIDsSorted = sorted(tagPostIDs.items(), reverse=True)

    maxPossiblePages = (len(tagPostIDsSorted) % loadLimit) + 2

    if page_number >= maxPossiblePages:
        return redirect("invalid")

    correctedPageNumber = maxPossiblePages - \
        1 if page_number >= maxPossiblePages else page_number

    startAt = correctedPageNumber * loadLimit

    hasNext = True

    if len(tagPostIDsSorted):
        if startAt >= len(tagPostIDsSorted):
            hasNext = False
            startAt = len(tagPostIDsSorted) - loadLimit + 1
        elif startAt < 0:
            startAt = 0

        for i in range(startAt, startAt + loadLimit):
            if i < len(tagPostIDsSorted):
                postID = tagPostIDsSorted[i][0]
                post = db.child("content/posts/" + postID).get().val()
                if post == None:
                    break
                post = dict(post)
                post["id"] = postID
                post["category"] = categories[post["category"]]

                allPosts.append(post)

        if len(allPosts) < loadLimit:
            hasNext = False

    return render(request, "site/pages/tags.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText, "posts": allPosts, "pageNum": correctedPageNumber, "pageNumForView": 1 + correctedPageNumber, "hasNext": hasNext})


def comingSoon(request):
    return render(request, "coming_soon.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText})


def invalid(request):
    return render(request, "invalid.html", {"isLoggedIn": "user" in request.session, "lang": request.session['lang'] if "lang" in request.session else "en", "staticTextMap": engAndPersianStaticText})

def privacyPolicy(request, lang):
    return render(request, f"site/pages/privacy_policy_{lang}.html")


def termsAndConditions(request, lang):
    return render(request, f"site/pages/terms_conditions_{lang}.html")

def logout(request):
    global loggedInUserCategoricalPosts
    loggedInUserCategoricalPosts = []

    request.session.pop('user', None)
    request.session.pop('uid', None)

    return redirect("home")
