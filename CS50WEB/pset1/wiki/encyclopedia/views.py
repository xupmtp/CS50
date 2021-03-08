import random

from django import forms
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse
from rest_framework import status

from . import util, matcher


class NewPage(forms.Form):
    title = forms.CharField(label="Title: ")

def index(request):
    return render(request, "encyclopedia/index.html", {
        "entries": util.list_entries()
    })


def get_entry(request, title):
    file = util.get_entry(title)
    # if no file return error page
    if not file:
        return render(request, "encyclopedia/error.html", {
            "status": status.HTTP_404_NOT_FOUND,
            "error_msg": f'Oops! Page <span style="color: red;">{title}</span> Not Found.'
        })
    content = matcher.convert_markdown(file)
    # return entry page
    return render(request, "encyclopedia/entry.html", {
        "title": title,
        "content": content
    })


def random_page():
    all_page = util.list_entries()
    rand_page = all_page[random.randint(0, len(all_page) - 1)]
    return HttpResponseRedirect(reverse(f'encyclopedia:entry', kwargs={
        "title": rand_page
    }))


def search_page(request):
    text = request.GET.get('q').strip()
    all_page = util.list_entries()
    if text in all_page:
        return HttpResponseRedirect(reverse('encyclopedia:entry', kwargs={
            "title": text
        }))
    entries = list(filter(lambda l: text in l, all_page))
    return render(request, "encyclopedia/search.html", {
        "entries": entries,
        "count": len(entries)
    })


def create_page(request):
    return None


def update_page(request):
    return None
