const {chromium} = require('playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');
(async () => {
 fs.mkdirSync('artifacts',{recursive:true});
 const browser=await chromium.launch();
 try {
  const page=await browser.newPage();
  const base=process.env.BASE_URL || 'http://localhost:8080';
  await page.goto(base);
  await page.waitForFunction(()=>document.querySelector('#status').textContent==='Estoque carregado.');
  await page.locator('#name').fill('Teclado de demonstração');
  await page.locator('#quantity').fill('10');
  await page.locator('#price').fill('120');
  await page.locator('#save').click();
  await page.waitForFunction(()=>document.querySelector('#status').textContent==='Produto cadastrado.');
  assert.equal(await page.locator('#total').innerText(),'1');
  await page.getByRole('button',{name:'Editar Teclado de demonstração',exact:true}).click();
  await page.locator('#quantity').fill('12');
  await page.locator('#save').click();
  await page.waitForFunction(()=>document.querySelector('#status').textContent==='Produto atualizado.');
  assert.equal(await page.locator('#units').innerText(),'12');
  await page.locator('#search').fill('não existe');
  assert.equal(await page.locator('#empty').isVisible(),true);
  await page.locator('#search').fill('');
  for(const width of [1440,390]) {
   await page.setViewportSize({width,height:1000});
   assert.equal(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth),true);
   await page.screenshot({path:`artifacts/estoque-${width}.png`,fullPage:true});
  }
  await page.getByRole('button',{name:'Excluir Teclado de demonstração',exact:true}).click();
  await page.locator('#delete-dialog button[value="cancel"]').click();
  assert.equal(await page.locator('#items tr').count(),1);
  await page.getByRole('button',{name:'Excluir Teclado de demonstração',exact:true}).click();
  await page.locator('#delete-dialog button[value="confirm"]').click();
  await page.waitForFunction(()=>document.querySelector('#status').textContent==='Produto excluído.');
  assert.equal(await page.locator('#items tr').count(),0);
  console.log('OK: cadastro, edição, filtro, indicadores, confirmação e exclusão.');
 } finally {await browser.close();}
})().catch(e=>{console.error(e);process.exitCode=1;});
