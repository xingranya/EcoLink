<template>
  <footer class="border-t border-primary/10 bg-white py-10">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-2 gap-8 md:grid-cols-4">
        <div class="col-span-2 md:col-span-1">
          <div class="mb-3 flex items-center gap-2 text-primary">
            <span class="material-symbols-outlined">eco</span>
            <span class="text-lg font-bold">EcoLink</span>
          </div>
          <p class="text-sm leading-relaxed text-slate-500">连接全国优质有机农产品，产地直供。触手可及的新鲜，值得信赖的品质。</p>
        </div>
        <div>
          <h5 class="mb-3 text-sm font-bold">选购指南</h5>
          <ul class="space-y-1.5 text-sm text-slate-500">
            <li><RouterLink class="hover:text-primary" to="/search">全部商品</RouterLink></li>
            <li><a class="hover:text-primary" href="#">配送说明</a></li>
            <li><a class="hover:text-primary" href="#">退换货政策</a></li>
          </ul>
        </div>
        <div>
          <h5 class="mb-3 text-sm font-bold">客户支持</h5>
          <ul class="space-y-1.5 text-sm text-slate-500">
            <li><a class="hover:text-primary" href="#">帮助中心</a></li>
            <li><a class="hover:text-primary" href="#">联系客服</a></li>
            <li><a class="hover:text-primary" href="#">隐私政策</a></li>
          </ul>
        </div>
        <div>
          <h5 class="mb-3 text-sm font-bold">订阅资讯</h5>
          <p class="mb-3 text-sm text-slate-500">获取时令上新和独家优惠。</p>
          <div class="flex h-9 overflow-hidden rounded-lg border border-primary/20">
            <input class="w-full border-none bg-background-light px-3 text-xs focus:ring-0" placeholder="邮箱地址" type="email" />
            <button class="bg-primary px-3 text-xs font-bold text-white">加入</button>
          </div>
        </div>
      </div>
      <div class="mt-8 border-t border-primary/10 pt-6 text-center text-xs text-slate-400">© {{ new Date().getFullYear() }} EcoLink 绿色生态农产品。保留所有权利。</div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>
