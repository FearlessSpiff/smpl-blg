<script>
import Polaroid from './Polaroid.vue'

export default {
  components: {Polaroid},
  data() {
    return {
      images: []
    }
  },
  created() {
    fetch('/api/v1/images')
        .then(response => response.json())
        .then(imageData => this.images = imageData)
        .catch((err) => {
          console.error(err);
        })
  }
}
</script>

<template>
  <div class="d-flex align-center flex-row flex-wrap">
    <div v-for="(image) in images" :key="image.id">
      <polaroid :image="image"/>
    </div>
  </div>
</template>


<style scoped>
</style>
