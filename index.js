document.getElementById('bookForm')
    .addEventListener('submit', async function(e) {
    e.preventDefault(); // フォームのデフォルト送信を防ぐ

    // フォームからデータを取得
    const bookData = {
        title: document.getElementById('title').value,
        price: document.getElementById('price').value
    };

    // APIにPOSTリクエストを送信して本を登録
    await fetch('/books/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookData),
    });

    // 本の一覧を再取得
    fetchBooks();
});

// 本の一覧を取得して表示
async function fetchBooks() {
    const response = await fetch('/');
    const books = await response.json();

    const bookList = document.getElementById('bookList');
    bookList.innerHTML = ''; // 一覧をクリア
    books.forEach(book => {
        const li = document.createElement('li');
        li.textContent = `${book.title} by ${book.author}`;
        bookList.appendChild(li);
    });
}

// 初期読み込み時に本の一覧を表示
fetchBooks();
