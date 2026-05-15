import{a as e,c as t,n,o as r,s as i,t as a}from"./DocumentFields-C0F1xt2d.js";var o={grip:`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="5" r="1"/><circle cx="9" cy="12" r="1"/><circle cx="9" cy="19" r="1"/><circle cx="15" cy="5" r="1"/><circle cx="15" cy="12" r="1"/><circle cx="15" cy="19" r="1"/></svg>`,edit:`<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>`,del:`<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4h6v2"/></svg>`,plus:`<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>`,x:`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,save:`<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>`,box:`<svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>`,reset:`<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>`};new class{fields=[];categories=[];activeCatId=``;selectedFieldId=null;editingField=null;isNew=!1;dragSrcId=null;toastTimer=null;catList;fieldList;fieldToolbarTitle;fieldCount;editPanel;epTitle;epBody;init(){this.fields=r(),this.categories=e(),this.categories.length>0&&(this.activeCatId=this.categories[0].id),this._buildSkeleton(),this._renderCategories(),this._renderFields(),document.addEventListener(`mousedown`,e=>{let t=document.getElementById(`ctx-menu`);t&&!t.contains(e.target)&&t.remove()})}_buildSkeleton(){document.body.innerHTML=`
<header class="fm-header">
  <h1>양식 필드 관리</h1>
  <span class="fm-header-spacer"></span>
  <button class="btn-header" id="btn-reset">${o.reset} 기본값 복원</button>
  <button class="btn-header primary" id="btn-save-all">${o.save} 저장</button>
</header>

<div class="fm-body">
  <aside class="fm-cat-panel">
    <div class="fm-cat-header">
      <span>카테고리</span>
      <button class="btn-icon-sm" id="btn-add-cat" title="카테고리 추가">${o.plus}</button>
    </div>
    <div class="fm-cat-list" id="cat-list"></div>
  </aside>

  <main class="fm-field-area">
    <div class="fm-field-toolbar">
      <span class="fm-field-title" id="field-title">-</span>
      <span class="fm-field-count" id="field-count"></span>
      <span class="fm-spacer"></span>
      <button class="btn-sm primary" id="btn-add-field">${o.plus} 필드 추가</button>
    </div>
    <div class="fm-field-list" id="field-list"></div>
  </main>
</div>

<aside class="fm-edit-panel" id="edit-panel">
  <div class="ep-header">
    <span class="ep-title" id="ep-title">필드 편집</span>
    <button class="btn-icon-sm" id="ep-close">${o.x}</button>
  </div>
  <div class="ep-body" id="ep-body"></div>
  <div class="ep-footer">
    <button class="btn-sm" id="ep-cancel">취소</button>
    <button class="btn-sm primary" id="ep-apply">${o.save} 저장</button>
  </div>
</aside>

<div class="fm-toast" id="fm-toast"></div>`,this.catList=document.getElementById(`cat-list`),this.fieldList=document.getElementById(`field-list`),this.fieldToolbarTitle=document.getElementById(`field-title`),this.fieldCount=document.getElementById(`field-count`),this.editPanel=document.getElementById(`edit-panel`),this.epTitle=document.getElementById(`ep-title`),this.epBody=document.getElementById(`ep-body`),document.getElementById(`btn-save-all`).addEventListener(`click`,()=>this._saveAll()),document.getElementById(`btn-reset`).addEventListener(`click`,()=>this._resetToDefaults()),document.getElementById(`btn-add-cat`).addEventListener(`click`,()=>this._openAddCategoryModal()),document.getElementById(`btn-add-field`).addEventListener(`click`,()=>this._openAddField()),document.getElementById(`ep-close`).addEventListener(`click`,()=>this._closeEditPanel()),document.getElementById(`ep-cancel`).addEventListener(`click`,()=>this._closeEditPanel()),document.getElementById(`ep-apply`).addEventListener(`click`,()=>this._applyEdit())}_renderCategories(){this.catList.innerHTML=this.categories.map(e=>{let t=this.fields.filter(t=>t.category===e.id).length,n=e.id===this.activeCatId,r=e.builtIn?`<span class="builtin-dot"></span>`:``;return`
<div class="fm-cat-item${n?` active`:``}" data-cat="${e.id}">
  ${r}
  <span>${e.label}</span>
  <span class="fm-cat-badge">${t}</span>
</div>`}).join(``),this.catList.querySelectorAll(`.fm-cat-item`).forEach(e=>{e.addEventListener(`click`,()=>{this.activeCatId=e.dataset.cat,this._renderCategories(),this._renderFields()}),e.addEventListener(`contextmenu`,t=>{t.preventDefault(),this._showCatContextMenu(t,e.dataset.cat)})})}_renderFields(){let e=this.categories.find(e=>e.id===this.activeCatId);this.fieldToolbarTitle.textContent=e?e.label:`-`;let t=this.fields.filter(e=>e.category===this.activeCatId).sort((e,t)=>(e.order??0)-(t.order??0));if(this.fieldCount.textContent=`(${t.length}개)`,t.length===0){this.fieldList.innerHTML=`
<div class="fm-empty">
  ${o.box}
  <p>이 카테고리에 필드가 없습니다.</p>
  <small>상단의 "필드 추가" 버튼을 눌러 첫 번째 필드를 추가하세요.</small>
</div>`;return}this.fieldList.innerHTML=t.map(e=>this._fieldRowHtml(e)).join(``),this.fieldList.querySelectorAll(`.fm-field-row`).forEach(e=>{let t=e.dataset.fid;e.addEventListener(`click`,e=>{e.target.closest(`.fm-field-actions`)||(this.selectedFieldId=t,this._openEditField(t))}),e.querySelector(`[data-edit]`)?.addEventListener(`click`,e=>{e.stopPropagation(),this._openEditField(t)}),e.querySelector(`[data-del]`)?.addEventListener(`click`,e=>{e.stopPropagation(),this._deleteField(t)}),e.setAttribute(`draggable`,`true`),e.addEventListener(`dragstart`,n=>{this.dragSrcId=t,e.classList.add(`dragging`),n.dataTransfer.effectAllowed=`move`}),e.addEventListener(`dragend`,()=>{e.classList.remove(`dragging`),this.dragSrcId=null,this.fieldList.querySelectorAll(`.drag-over`).forEach(e=>e.classList.remove(`drag-over`))}),e.addEventListener(`dragover`,n=>{n.preventDefault(),n.dataTransfer.dropEffect=`move`,t!==this.dragSrcId&&(this.fieldList.querySelectorAll(`.drag-over`).forEach(e=>e.classList.remove(`drag-over`)),e.classList.add(`drag-over`))}),e.addEventListener(`dragleave`,()=>e.classList.remove(`drag-over`)),e.addEventListener(`drop`,n=>{n.preventDefault(),e.classList.remove(`drag-over`),this.dragSrcId&&this.dragSrcId!==t&&this._reorderField(this.dragSrcId,t)})})}_fieldRowHtml(e){let t=`<span class="type-badge ${e.type}">${this._typeLabel(e.type)}</span>`,n=e.builtIn?`<span class="builtin-badge">기본</span>`:``;return`
<div class="fm-field-row" data-fid="${e.id}">
  <span class="drag-handle">${o.grip}</span>
  <span class="fm-field-label">${e.label}</span>
  <span class="fm-field-placeholder">${e.placeholder}</span>
  ${t}
  ${n}
  <div class="fm-field-actions">
    <button class="btn-icon-sm" data-edit title="편집">${o.edit}</button>
    ${e.builtIn?``:`<button class="btn-icon-sm danger" data-del title="삭제">${o.del}</button>`}
  </div>
</div>`}_typeLabel(e){return e===`text`?`텍스트`:e===`number`?`숫자`:`날짜`}_openAddField(){let e=Math.max(0,...this.fields.map(e=>e.order??0));this.editingField={id:``,label:``,placeholder:``,type:`text`,category:this.activeCatId,builtIn:!1,order:e+1},this.isNew=!0,this.epTitle.textContent=`필드 추가`,this._renderEditForm(),this.editPanel.classList.add(`open`)}_openEditField(e){let t=this.fields.find(t=>t.id===e);t&&(this.editingField={...t},this.isNew=!1,this.epTitle.textContent=`필드 편집`,this._renderEditForm(),this.editPanel.classList.add(`open`))}_renderEditForm(){if(!this.editingField)return;let e=this.editingField,t=this.categories.map(t=>`<option value="${t.id}"${t.id===e.category?` selected`:``}>${t.label}</option>`).join(``),r=[`text`,`number`,`date`].map(t=>`<option value="${t}"${t===e.type?` selected`:``}>${this._typeLabel(t)}</option>`).join(``),i=e.type===`number`?`<div class="form-group">
           <label>숫자 포맷</label>
           <select id="ep-fmt">
             ${n.map(([t,n])=>`<option value="${t}"${e.defaultNumberFormat===t?` selected`:``}>${n}</option>`).join(``)}
           </select>
         </div>`:e.type===`date`?`<div class="form-group">
             <label>날짜 포맷</label>
             <select id="ep-fmt">
               ${a.map(([t,n])=>`<option value="${t}"${e.defaultNumberFormat===t?` selected`:``}>${n}</option>`).join(``)}
             </select>
           </div>`:``;this.epBody.innerHTML=`
<div class="form-group">
  <label>ID (영문·숫자·밑줄)</label>
  <input id="ep-id" value="${e.id}" ${e.builtIn?`readonly`:``} placeholder="예) my_field" />
  ${e.builtIn?``:`<span class="form-hint">생성 후 변경 불가. 영문 소문자, 숫자, 밑줄만 허용.</span>`}
</div>
<div class="form-group">
  <label>표시 이름</label>
  <input id="ep-label" value="${e.label}" placeholder="예) 계약 금액" />
</div>
<div class="form-group">
  <label>플레이스홀더</label>
  <input id="ep-placeholder" value="${e.placeholder}" placeholder="예) $.{my_field}" />
</div>
<div class="form-group">
  <label>카테고리</label>
  <select id="ep-cat">${t}</select>
</div>
<hr class="form-divider" />
<div class="form-group">
  <label>필드 유형</label>
  <select id="ep-type">${r}</select>
</div>
${i}`,this.epBody.querySelector(`#ep-type`)?.addEventListener(`change`,e=>{this.editingField&&(this.editingField.type=e.target.value,this.editingField.defaultNumberFormat=void 0,this._syncEditFormToState(),this._renderEditForm())})}_syncEditFormToState(){if(!this.editingField)return;let e=e=>(this.epBody.querySelector(`#${e}`)?.value??``).trim();this.editingField.builtIn||(this.editingField.id=e(`ep-id`)),this.editingField.label=e(`ep-label`),this.editingField.placeholder=e(`ep-placeholder`),this.editingField.category=e(`ep-cat`),this.editingField.type=e(`ep-type`);let t=this.epBody.querySelector(`#ep-fmt`);t&&(this.editingField.defaultNumberFormat=t.value)}_applyEdit(){if(this._syncEditFormToState(),!this.editingField)return;let e=this.editingField;if(!e.label.trim()){this._toast(`표시 이름을 입력하세요.`,`error`);return}if(!e.id.trim()){this._toast(`ID를 입력하세요.`,`error`);return}if(!/^[a-z0-9_]+$/.test(e.id)){this._toast(`ID는 영문 소문자, 숫자, 밑줄만 허용됩니다.`,`error`);return}if(this.isNew){if(this.fields.some(t=>t.id===e.id)){this._toast(`ID "${e.id}" 이(가) 이미 존재합니다.`,`error`);return}this.fields.push({...e})}else{let t=this.fields.findIndex(t=>t.id===e.id);t!==-1&&(this.fields[t]={...e})}this._closeEditPanel(),this._renderCategories(),this._renderFields(),this._toast(this.isNew?`필드가 추가되었습니다.`:`필드가 수정되었습니다.`,`success`)}_closeEditPanel(){this.editPanel.classList.remove(`open`),this.editingField=null}_deleteField(e){let t=this.fields.find(t=>t.id===e);if(t){if(t.builtIn){this._toast(`기본 제공 필드는 삭제할 수 없습니다.`,`error`);return}confirm(`"${t.label}" 필드를 삭제하시겠습니까?`)&&(this.fields=this.fields.filter(t=>t.id!==e),this.selectedFieldId===e&&(this.selectedFieldId=null),this._renderCategories(),this._renderFields(),this._toast(`필드가 삭제되었습니다.`,`success`))}}_reorderField(e,t){let n=this.fields.filter(e=>e.category===this.activeCatId).sort((e,t)=>(e.order??0)-(t.order??0)),r=n.findIndex(t=>t.id===e),i=n.findIndex(e=>e.id===t);if(r===-1||i===-1)return;let[a]=n.splice(r,1);n.splice(i,0,a),n.forEach((e,t)=>{let n=this.fields.find(t=>t.id===e.id);n&&(n.order=t*10+this.categories.findIndex(e=>e.id===this.activeCatId)*1e3)}),this._renderFields()}_showCatContextMenu(e,t){let n=document.getElementById(`ctx-menu`);n&&n.remove();let r=this.categories.find(e=>e.id===t);if(!r)return;let i=document.createElement(`div`);i.id=`ctx-menu`,i.className=`ctx-menu`,i.innerHTML=`
<div class="ctx-menu-item" data-action="rename">이름 변경</div>
${r.builtIn?``:`<hr class="ctx-menu-sep"><div class="ctx-menu-item danger" data-action="delete">카테고리 삭제</div>`}`,i.style.left=`${e.clientX}px`,i.style.top=`${e.clientY}px`,document.body.appendChild(i),i.addEventListener(`click`,e=>{let n=e.target.closest(`[data-action]`)?.dataset.action;i.remove(),n===`rename`&&this._renameCategory(t),n===`delete`&&this._deleteCategory(t)})}_renameCategory(e){let t=this.categories.find(t=>t.id===e);if(!t)return;let n=prompt(`새 카테고리 이름:`,t.label);!n||!n.trim()||(t.label=n.trim(),this._renderCategories(),this._renderFields(),this._toast(`카테고리 이름이 변경되었습니다.`,`success`))}_deleteCategory(e){let t=this.categories.find(t=>t.id===e);if(!t)return;if(t.builtIn){this._toast(`기본 제공 카테고리는 삭제할 수 없습니다.`,`error`);return}let n=this.fields.filter(t=>t.category===e).length;if(n>0){if(!confirm(`"${t.label}" 카테고리에 ${n}개의 필드가 있습니다.\n카테고리를 삭제하면 필드도 함께 삭제됩니다. 계속하시겠습니까?`))return;this.fields=this.fields.filter(t=>t.category!==e)}else if(!confirm(`"${t.label}" 카테고리를 삭제하시겠습니까?`))return;this.categories=this.categories.filter(t=>t.id!==e),this.activeCatId===e&&(this.activeCatId=this.categories[0]?.id??``),this._renderCategories(),this._renderFields(),this._toast(`카테고리가 삭제되었습니다.`,`success`)}_openAddCategoryModal(){let e=document.createElement(`div`);e.className=`modal-backdrop`,e.innerHTML=`
<div class="modal-box" id="add-cat-box">
  <div class="modal-title">카테고리 추가</div>
  <div class="form-group">
    <label>카테고리 ID (영문·숫자·밑줄)</label>
    <input id="new-cat-id" placeholder="예) my_category" />
    <span class="form-hint">영문 소문자, 숫자, 밑줄만 허용됩니다.</span>
  </div>
  <div class="form-group">
    <label>표시 이름</label>
    <input id="new-cat-label" placeholder="예) 내 카테고리" />
  </div>
  <div class="modal-actions">
    <button class="btn-sm" id="cat-modal-cancel">취소</button>
    <button class="btn-sm primary" id="cat-modal-ok">${o.plus} 추가</button>
  </div>
</div>`,document.body.appendChild(e),e.querySelector(`#new-cat-id`).focus();let t=()=>e.remove();e.addEventListener(`mousedown`,n=>{n.target===e&&t()}),e.querySelector(`#cat-modal-cancel`)?.addEventListener(`click`,t),e.querySelector(`#cat-modal-ok`)?.addEventListener(`click`,()=>{let n=e.querySelector(`#new-cat-id`).value.trim(),r=e.querySelector(`#new-cat-label`).value.trim();if(!n){this._toast(`카테고리 ID를 입력하세요.`,`error`);return}if(!/^[a-z0-9_]+$/.test(n)){this._toast(`ID는 영문 소문자, 숫자, 밑줄만 허용됩니다.`,`error`);return}if(!r){this._toast(`표시 이름을 입력하세요.`,`error`);return}if(this.categories.some(e=>e.id===n)){this._toast(`ID "${n}" 이(가) 이미 존재합니다.`,`error`);return}let i=Math.max(0,...this.categories.map(e=>e.order));this.categories.push({id:n,label:r,order:i+1,builtIn:!1}),this.activeCatId=n,t(),this._renderCategories(),this._renderFields(),this._toast(`카테고리가 추가되었습니다.`,`success`)})}_saveAll(){t(this.fields),i(this.categories),this._toast(`저장되었습니다.`,`success`)}_resetToDefaults(){confirm(`모든 사용자 정의 필드와 카테고리를 삭제하고 기본값으로 복원하시겠습니까?`)&&(localStorage.removeItem(`poa-field-definitions`),localStorage.removeItem(`poa-field-categories`),this.fields=r(),this.categories=e(),this.activeCatId=this.categories[0]?.id??``,this._renderCategories(),this._renderFields(),this._toast(`기본값으로 복원되었습니다.`,`success`))}_toast(e,t=`info`){let n=document.getElementById(`fm-toast`);this.toastTimer!==null&&clearTimeout(this.toastTimer),n.textContent=e,n.className=`fm-toast ${t} show`,this.toastTimer=window.setTimeout(()=>{n.classList.remove(`show`),this.toastTimer=null},2500)}}().init();