import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FooterComponent } from '../../shared/components/footer/footer.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, FooterComponent, CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  @ViewChild('menu') menuElement!: ElementRef;

  isMenuOpenMobile = false;
  isMenuOpenUser = false;
  scrolled = false;

  toggleMenu(event?: Event) {
    event?.stopPropagation();
    this.isMenuOpenMobile = !this.isMenuOpenMobile;
  }

  toggleMenuUser(event?: Event) {
    event?.stopPropagation();
    this.isMenuOpenUser = !this.isMenuOpenUser;
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.scrolled = window.scrollY > 200; // Cambia el valor según tus necesidades
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.handleResize();
  }

  handleResize() {
    if (window.innerWidth >= 800 && this.isMenuOpenMobile) {
      this.toggleMenu();
    }
    if (window.innerWidth <= 800 && this.isMenuOpenUser) {
      this.toggleMenuUser();
    }
  }

  @HostListener('document:click', ['$event'])
  onClick(event: Event) {
    if (
      this.isMenuOpenUser &&
      !this.menuElement.nativeElement.contains(event.target)
    ) {
      this.isMenuOpenUser = false;
    }
    if (
      this.isMenuOpenMobile &&
      !this.menuElement.nativeElement.contains(event.target)
    ) {
      this.isMenuOpenMobile = false;
    }
  }
}
