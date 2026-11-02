const list = document.querySelector('#task-list');
let tasks = [];
let filter = 'all';
const escapeHtml = (value) => value.replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

async function request(path, options = {}) {
  const response = await fetch(path, {headers:{'Content-Type':'application/json'}, ...options});
  if (!response.ok) throw new Error((await response.json()).error || 'Something went wrong');
  return response.status === 204 ? null : response.json();
}
async function refresh() {
  try { tasks = await request('/api/todos'); render(); }
  catch { list.innerHTML = '<div class="empty"><span>☁</span>Could not reach the app. Refresh to try again.</div>'; }
}
function render() {
  const done = tasks.filter(t => t.completed).length;
  const open = tasks.length - done;
  const percent = tasks.length ? Math.round(done / tasks.length * 100) : 0;
  document.querySelector('#open-stat').textContent = open;
  document.querySelector('#done-stat').textContent = done;
  document.querySelector('#total-stat').textContent = tasks.length;
  document.querySelector('#task-total').textContent = tasks.length;
  document.querySelector('#nav-count').textContent = open;
  document.querySelector('#progress-value').textContent = `${percent}%`;
  document.querySelector('#progress-ring').style.background = `conic-gradient(#7d9d80 ${percent}%, #edf0eb 0)`;
  document.querySelector('#progress-copy').textContent = tasks.length ? (percent === 100 ? 'Everything is done!' : `${done} of ${tasks.length} completed`) : 'Add your first task';
  const visible = tasks.filter(t => filter === 'all' || (filter === 'active' ? !t.completed : t.completed));
  list.innerHTML = visible.length ? visible.map(t => `<article class="task ${t.completed?'completed':''}" data-id="${t.id}"><button class="check" aria-label="${t.completed?'Mark active':'Mark complete'}">✓</button><span class="task-title">${escapeHtml(t.title)}</span><span class="task-tag">${t.completed?'Done':'Personal'}</span><button class="delete" aria-label="Delete task">×</button></article>`).join('') : `<div class="empty"><span>${filter === 'completed'?'✿':'✦'}</span>${filter === 'completed'?'Completed tasks will appear here.':'A clear list is a fresh start. Add a task above.'}</div>`;
}
document.querySelector('#add-form').addEventListener('submit', async event => {
  event.preventDefault(); const input = document.querySelector('#new-title'); const title = input.value.trim(); if (!title) return;
  try { await request('/api/todos', {method:'POST', body:JSON.stringify({title})}); input.value=''; await refresh(); }
  catch (error) { alert(error.message); }
});
list.addEventListener('click', async event => {
  const task = event.target.closest('.task'); if (!task) return;
  const item = tasks.find(t => t.id === Number(task.dataset.id)); if (!item) return;
  try {
    if (event.target.closest('.check')) await request(`/api/todos/${item.id}`, {method:'PATCH', body:JSON.stringify({completed:!item.completed})});
    else if (event.target.closest('.delete')) await request(`/api/todos/${item.id}`, {method:'DELETE'});
    else return;
    await refresh();
  } catch (error) { alert(error.message); }
});
document.querySelectorAll('.filter').forEach(button => button.addEventListener('click', () => {document.querySelector('.filter.active').classList.remove('active');button.classList.add('active');filter=button.dataset.filter;render();}));
document.querySelector('#today-date').textContent = new Intl.DateTimeFormat(undefined,{weekday:'short',month:'short',day:'numeric'}).format(new Date());
refresh();
