from django.urls import path

from . import views

app_name = 'encyclopedia'
urlpatterns = [
    path("", views.index, name="index"),
    path("random_page", views.random_page, name="random_page"),
    path("search", views.search_page, name="search"),
    path("create", views.create_page, name="create"),
    path("update", views.update_page, name="update"),
    path("<str:title>", views.get_entry, name="entry")
]
