import random

from django import forms
from django.forms import TextInput
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse
from rest_framework import status

from . import util, matcher


class NewPage ( forms.Form ):
    title = forms.CharField ( label="Title",
                              strip=True,
                              widget=TextInput ( attrs={
                                  "class": "form-control"
                              } ) )
    content = forms.CharField ( label="",
                                required=True,
                                widget=forms.Textarea ( attrs={
                                    "placeholder": "Write your page markdown",
                                    "class": "form-control",
                                } )
                                )

    def set_default_value(self, title, content):
        self.fields['title'].widget = TextInput ( attrs={
            "class": "form-control",
            "value": title
        } )
        self.fields['content'].initial = content


def index(request):
    return render ( request, "encyclopedia/index.html", {
        "entries": util.list_entries ()
    } )


def get_entry(request, title):
    file = util.get_entry ( title )
    # if no file return error page
    if not file:
        return render ( request, "encyclopedia/error.html", {
            "status": status.HTTP_404_NOT_FOUND,
            "error_msg": f'Oops! Page <span style="color: red;">{title}</span> Not Found.'
        } )
    content = matcher.convert_markdown ( file )
    # return entry page
    return render ( request, "encyclopedia/entry.html", {
        "title": title,
        "content": content
    } )


def random_page(request):
    all_page = util.list_entries ()
    rand_page = all_page[random.randint ( 0, len ( all_page ) - 1 )]
    return HttpResponseRedirect ( reverse ( 'encyclopedia:entry', kwargs={
        "title": rand_page
    } ) )


def search_page(request):
    text = request.GET.get ( 'q' ).strip ()
    all_page = util.list_entries ()
    if text in all_page:
        return HttpResponseRedirect ( reverse ( 'encyclopedia:entry', kwargs={
            "title": text
        } ) )
    entries = list ( filter ( lambda l: text in l, all_page ) )
    return render ( request, "encyclopedia/search.html", {
        "entries": entries,
        "count": len ( entries )
    } )


def create_page(request):
    if request.method == "POST":
        form = NewPage ( request.POST )
        print ( form.is_valid () )
        if not form.is_valid ():
            return render ( request, "encyclopedia/error.html", {
                "status": status.HTTP_403_FORBIDDEN,
                "error_msg": "Invalid content."
            } )
        title = form.cleaned_data['title']
        content = form.cleaned_data['content']
        if title in util.list_entries ():
            new_form = NewPage()
            new_form.set_default_value(title, content)
            return render ( request, "encyclopedia/edit_page.html", {
                "form": new_form,
                "error_msg": "Duplicate title.",
            } )
        util.save_entry ( title, content )
        return HttpResponseRedirect ( reverse ( 'encyclopedia:entry', kwargs={
            "title": title
        } ) )
    return render ( request, "encyclopedia/edit_page.html", {
        "form": NewPage ()
    } )


def update_page(request):
    if request.method == "POST":
        title = request.POST.get ( 'title' )
        content = request.POST.get ( 'content' )
        print (content)
        if not title or not content:
            return render ( request, "encyclopedia/error.html", {
                "status": status.HTTP_500_INTERNAL_SERVER_ERROR,
                "error_msg": f'Oops! Update entry page error!.'
            } )
        util.save_entry ( title, content )
        return HttpResponseRedirect ( reverse ( 'encyclopedia:entry', kwargs={
            "title": title
        } ) )
    get_title = request.GET.get ( 'title' )
    page_form = util.get_entry ( get_title )
    return render ( request, "encyclopedia/update_page.html", {
        "title": get_title,
        "content": page_form
    } )
