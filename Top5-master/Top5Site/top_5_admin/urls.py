from django.urls import path
from . import views

urlpatterns = [
    path('', views.admin_auth, name='admin_auth'),
    path('dashboard/', views.dashboard, name='dashboard'),
    path('logout', views.admin_logout, name="admin_logout"),
    path('myDash/', views.myDash, name='myDash'),
    path('categoryDash/<str:category_id>',
         views.categoryDash, name='categoryDash'),
    path('postPreview/', views.postPreview, name='postPreview')
]
