import re


def replace_href(m):
    print(m.groups())
    return f'<a href="{m.group(5).strip()}">{m.group(2).strip()}</a>'


# 轉換連結
def links(text):
    return re.findall(r'(\[)([^\[\]]+)(])(\()([^\(\)]+)(\))', text)

aa = ['qqq', 'asdf']
print('qqq' in aa)