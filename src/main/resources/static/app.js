'use strict';
const $ = selector => document.querySelector(selector);
let items = [];
let editingId = null;
let busy = false;
const currency = new Intl.NumberFormat('pt-BR', {style:'currency', currency:'BRL'});
function status(message, error = false) {
  $('#status').textContent = message;
  $('#status').classList.toggle('error', error);
}
function setBusy(value) {
  busy = value;
  document.querySelectorAll('button').forEach(button => { button.disabled = value; });
}
async function request(path, options = {}) {
  const response = await fetch(path, {...options, headers: {...(options.body ? {'Content-Type':'application/json'} : {}), ...options.headers}});
  if (!response.ok) {
    let detail;
    try { detail = await response.json(); } catch { detail = {}; }
    const fields = detail.campos?.map(field => `${field.campo}: ${field.mensagem}`).join('; ');
    throw new Error(fields || detail.mensagem || `Não foi possível concluir a operação (${response.status}).`);
  }
  return response.status === 204 ? null : response.json();
}
function resetForm() {
  editingId = null;
  $('#item-form').reset();
  $('#quantity').value = '0';
  $('#form-title').textContent = 'Novo produto';
  $('#save').textContent = 'Cadastrar produto';
  $('#cancel').hidden = true;
}
function render() {
  const query = $('#search').value.trim().toLocaleLowerCase('pt-BR');
  const visible = items.filter(item => item.nome.toLocaleLowerCase('pt-BR').includes(query));
  $('#total').textContent = String(items.length);
  $('#units').textContent = String(items.reduce((sum, item) => sum + item.quantidade, 0));
  $('#value').textContent = currency.format(items.reduce((sum, item) => sum + Math.round(item.preco * 100) * item.quantidade, 0) / 100);
  $('#items').replaceChildren();
  visible.forEach(item => {
    const row = document.createElement('tr');
    [item.nome, String(item.quantidade), currency.format(item.preco)].forEach(value => {
      const cell = document.createElement('td');
      cell.textContent = value;
      row.append(cell);
    });
    const actions = document.createElement('td');
    for (const [label, handler, className] of [['Editar', () => edit(item), 'secondary'], ['Excluir', () => confirmDelete(item), 'secondary']]) {
      const button = document.createElement('button');
      button.type = 'button';
      button.className = className;
      button.textContent = label;
      button.setAttribute('aria-label', `${label} ${item.nome}`);
      button.disabled = busy;
      button.addEventListener('click', handler);
      actions.append(button);
    }
    row.append(actions);
    $('#items').append(row);
  });
  $('#empty').hidden = visible.length !== 0;
}
async function load() {
  items = await request('/itens');
  render();
}
function edit(item) {
  if (busy) return;
  editingId = item.id;
  $('#name').value = item.nome;
  $('#quantity').value = item.quantidade;
  $('#price').value = item.preco;
  $('#form-title').textContent = 'Editar produto';
  $('#save').textContent = 'Salvar alterações';
  $('#cancel').hidden = false;
  $('#name').focus();
}
function confirmDelete(item) {
  if (busy) return;
  const dialog = $('#delete-dialog');
  $('#delete-description').textContent = `O produto “${item.nome}” será removido do estoque.`;
  dialog.returnValue = 'cancel';
  dialog.addEventListener('close', async () => {
    if (dialog.returnValue !== 'confirm') return;
    setBusy(true);
    try {
      await request(`/itens/${item.id}`, {method:'DELETE'});
      if (editingId === item.id) resetForm();
      await load();
      status('Produto excluído.');
    } catch (error) { status(error.message, true); }
    finally { setBusy(false); }
  }, {once:true});
  dialog.showModal();
}
$('#item-form').addEventListener('submit', async event => {
  event.preventDefault();
  if (busy || !event.currentTarget.reportValidity()) return;
  const nome = $('#name').value.trim();
  if (!nome) { $('#name').setCustomValidity('Informe um nome para o produto.'); $('#name').reportValidity(); return; }
  setBusy(true);
  const wasEditing = editingId !== null;
  try {
    await request(wasEditing ? `/itens/${editingId}` : '/itens', {
      method:wasEditing ? 'PUT' : 'POST',
      body:JSON.stringify({nome, quantidade:Number($('#quantity').value), preco:Number($('#price').value)})
    });
    resetForm();
    await load();
    status(wasEditing ? 'Produto atualizado.' : 'Produto cadastrado.');
  } catch (error) { status(error.message, true); }
  finally { setBusy(false); }
});
$('#name').addEventListener('input', () => $('#name').setCustomValidity(''));
$('#cancel').addEventListener('click', resetForm);
$('#search').addEventListener('input', render);
$('#refresh').addEventListener('click', async () => {
  setBusy(true);
  try { await load(); status('Lista atualizada.'); }
  catch (error) { status(error.message, true); }
  finally { setBusy(false); }
});
setBusy(true);
load().then(() => status('Estoque carregado.')).catch(error => status(`Não foi possível carregar o estoque. ${error.message}`, true)).finally(() => setBusy(false));
