import re
from functools import reduce
"""
Convert markdown to html by myself
"""

def replace_strong(m):
    return f'<strong>{m.group(2).strip()}</strong>'


def replace_href(m):
    return f'<a href="{m.group(5).strip()}">{m.group(2).strip()}</a>'


# 轉換粗體
def boldface(text):
    return re.sub(r'(\*{2})([^\*]+)(\*{2})', replace_strong, text)


# 轉換連結
def links(text):
    return re.sub(r'(\[)([^\[\]]+)(])(\()([^\(\)]+)(\))', replace_href, text)


# 轉換連結和粗體
def replace_strong_and_a(text):
    return links(boldface(text))


# markdown轉html
def convert_markdown(markdown):
    res = ""
    li_list = []
    is_paragraphs = False

    if not markdown:
        return None

    for text in markdown.splitlines():
        match_header = re.match(r'#{1,6}\s', text)
        match_list = re.match(r'\*\s', text)
        list_flag = match_list is None

        # 不匹配li且li_list有內容代表一個完整ul
        if list_flag and li_list:
            res += f'<ul>{reduce ( lambda a, b: a + b, li_list )}</ul>'
            li_list = []

        if not text:
            is_paragraphs = True
            continue
        # 轉標題文字
        if match_header:
            h_count = match_header.end() - 1
            html = f'<h{h_count}>{text.replace("#", "", h_count).strip()}</h{h_count}>'
            res += links(html)
        # 轉ul,li
        elif match_list:
            html = text.replace("*", "", 1).strip()
            html = f'<p>{html}</p>' if is_paragraphs and li_list else html
            if li_list and '<p>' not in li_list[len(li_list) -1]:
                li_list[len(li_list) -1] = f'<p>{li_list[len(li_list) -1]}</p>'
            li_list.append(replace_strong_and_a(f'<li>{html}</li>'))
        # 其他內容
        else:
            res += replace_strong_and_a(f'<p>{text}</p>' if is_paragraphs else text)
        is_paragraphs = False

    # 若li在文件尾部要再轉一次
    if li_list:
        res += f'<ul>{reduce ( lambda a, b: a + b, li_list )}</ul>'
    return res
