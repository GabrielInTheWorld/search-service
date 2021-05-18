import json
import random
import sys

words = None
amount = None
try:
    amount = int(sys.argv[1])
except:
    amount = 10

def init_words():
    global words
    words = []
    file = open("./words.txt", "r")
    for line in file:
        words.append(line[0:line.find('\n') if line.find('\n') > -1 else ''])
    file.close()

def get_words():
    global words 
    if words is None:
        init_words()
    return words

def randstr(length):
    words = get_words()
    result = ""
    for i in range(length):
        word = words[random.randint(0, len(words) - 1)]
        result += f" {word} "
    return result

topics = []
for id in range(1, amount + 1):
    topics.append({
        "id": id,
        "title": "topic " + str(id),
        "text": randstr(100)
    })

blocks = []
for id in range(1, amount + 1):
    blocks.append({
        "id": id,
        "title": "block " + str(id),
        "internal": bool(random.getrandbits(1))
    })

document = {
    "topic": topics,
    "motion_block": blocks
}

print(json.dumps(document))
