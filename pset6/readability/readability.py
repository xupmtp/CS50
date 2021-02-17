import math


def main():
    letters, words, sentences = 0, 1, 0
    sentence_test = ["!", ".", "?"]

    text = input("Text: ")
    for i in text:
        letters += 1 if i.isalpha() else 0
        words += 1 if i.isspace() else 0
        sentences += 1 if i in sentence_test else 0

    print(letters, words, sentences)
    w_by_l = (letters / words) * 100
    sentence_by_words = (sentences / words) * 100
    index = (0.0588 * w_by_l) - (0.296 * sentence_by_words) - 15.8
    index = round(index)
    print_grade(index)


def print_grade(index):
    if index < 1:
        print("Before Grade 1")
    elif index >= 16:
        print("Grade 16+")
    else:
        print(f"Grade {index}")


main()
