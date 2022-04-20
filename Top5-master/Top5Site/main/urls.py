from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name="home"),
    path('categories/', views.categories, name="categories"),
    path('categories/<str:category_id>/', views.categories, name="categories"),
    path('categories/<str:category_id>/<int:page_number>/',
         views.categories, name="categories"),
    path('profile/', views.profile, name="profile"),
    path('privacy/<str:lang>', views.privacyPolicy, name="privacyPolicy"),
    path('terms/<str:lang>', views.termsAndConditions, name="termsAndConditions"),
    path('browse/', views.browse, name="browse"),
    path('browse/<str:searchTerm>/', views.browse, name="browse"),
    path('tags/<str:tag>/', views.tags, name="tags"),
    path('tags/<str:tag>/<int:page_number>/', views.tags, name="tags"),
    path('saved/<int:page_number>/', views.saved, name="saved"),
    path('posts/<str:post_title_id>/', views.post, name="post"),
    path('auth/', views.credentials, name='credentials'),
    path('logout/', views.logout, name="logout"),
    path('comingSoon/', views.comingSoon, name='comingSoon'),
    path('invalid_page/', views.invalid, name="invalid")
]
